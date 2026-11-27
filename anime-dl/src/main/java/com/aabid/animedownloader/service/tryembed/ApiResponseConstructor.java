package com.aabid.animedownloader.service.tryembed;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.jspecify.annotations.NonNull;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

class ApiResponseConstructor {

    private static final ObjectMapper mapper = new ObjectMapper();

    private byte[] key;

    ApiResponseConstructor(@NonNull String key) {
        this.key = hexToBytes(key);
    }

    @NonNull
    ApiResponse constructResponse(@NonNull String jsonStr) throws IOException {
        JsonNode tree = mapper.readTree(jsonStr);
        if (isPlainText(tree)) {
            return mapper.treeToValue(tree, ApiResponse.class);
        }

        try {
            byte[] iv = Base64.getDecoder().decode(tree.get("iv").asString());
            byte[] encryptedData = Base64.getDecoder().decode(tree.get("data").asString());

            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedData);

            String json = new String(decryptedBytes, StandardCharsets.UTF_8);
            JsonNode result = mapper.readTree(json);

            if (tree.has("embedNonce") && !result.has("embedNonce")) {
                if (result instanceof ObjectNode) {
                    ((ObjectNode) result).set("embedNonce", tree.get("embedNonce"));
                }
            }

            return mapper.treeToValue(result, ApiResponse.class);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new IOException("Error while decrypting response", e);
        }
    }

    private static boolean isPlainText(JsonNode tree) {
        boolean encrypted = tree.hasNonNull("_enc") && tree.get("_enc").asInt() == 1;
        if (!encrypted) {
            return true;
        }

        if (!tree.hasNonNull("data") || tree.get("data").asString().isEmpty()) {
            throw new IllegalArgumentException(
                "Encrypted response do not have 'data' field. It may be caused by server side changes"
            );
        }

        if (!tree.hasNonNull("iv") || tree.get("iv").asString().isEmpty()) {
            throw new IllegalArgumentException(
                "Encrypted response do not have 'iv' field. It may be caused by server side changes"
            );
        }

        return false;
    }

    private static byte[] hexToBytes(String hex) {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hexadecimal key length");
        }

        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }

        return bytes;
    }
}
