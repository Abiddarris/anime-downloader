package com.aabid.animedownloader.utils.format;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.utils.format.Token.Type;

class Tokenizer {

    @NonNull
    static List<@NonNull Token> tokenize(String format) {
        List<@NonNull Token> tokens = new ArrayList<>();
        for (int i = 0; i < format.length();) {
            char c = format.charAt(i);
            if (!(c == '{' || c == '}')) {
                tokens.add(createLiteralToken(c));
                i++;
                continue;
            }

            if (i + 1 >= format.length()) {
                tokens.add(createCurlyBracketToken(c));
                i++;
                continue;
            }

            char nextChar = format.charAt(i + 1);
            if (nextChar == c) {
                tokens.add(createLiteralToken(c));
                i += 2;
                continue;
            }

            tokens.add(createCurlyBracketToken(c));
            i++;
        }
        return tokens;
    }

    @NonNull
    private static Token createLiteralToken(char c) {
        return new Token(c, Type.LITERAL);
    }

    @NonNull
    private static Token createCurlyBracketToken(char c) {
        if (c == '{') {
            return new Token(c, Type.BRACKET_START);
        }

        return new Token(c, Type.BRACKET_END);
    }
}
