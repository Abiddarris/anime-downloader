/*
 * Copyright 2026 Aabid Darris
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aabid.animedownloader.service.tryembed;

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
