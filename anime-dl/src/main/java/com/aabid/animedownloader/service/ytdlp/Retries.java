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