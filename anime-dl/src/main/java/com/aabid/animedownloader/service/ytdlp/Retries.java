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
package com.aabid.animedownloader.service.ytdlp;

import com.abiddarris.common.utils.Preconditions;

public final class Retries {

    private boolean infinite;
    private long retries;

    private static final Retries INFINITE = new Retries();

    private Retries() {
        this.infinite = true;
    }

    private Retries(long retries) {
        Preconditions.checkNonNegative(retries, "retries must >= 0");

        this.retries = retries;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public long getRetries() {
        if (infinite)
            throw new IllegalStateException("infinite retries do not have a number");

        return retries;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (infinite ? 1231 : 1237);
        result = prime * result + (int) (retries ^ (retries >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Retries other = (Retries) obj;
        if (infinite != other.infinite)
            return false;
        if (retries != other.retries)
            return false;
        return true;
    }

    public static Retries of(long retries) {
        return new Retries(retries);
    }

    public static Retries infinite() {
        return INFINITE;
    }
}