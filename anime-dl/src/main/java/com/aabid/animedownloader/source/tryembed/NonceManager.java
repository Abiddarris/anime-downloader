package com.aabid.animedownloader.source.tryembed;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

class NonceManager {

    @Nullable
    private String nonce;

    public NonceManager(@NonNull String initial) {
        this.nonce = initial;
    }

    public String acquire() {
        String nonce = this.nonce;
        if (nonce == null) {
            throw new IllegalStateException("Unable to acquire nonce. It has not been updated.");
        }
        this.nonce = null;

        return nonce;
    }

    public void update(@NonNull String nonce) {
        this.nonce = nonce;
    }
}
