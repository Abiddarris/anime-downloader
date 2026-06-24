package com.aabid.animedownloader.source;

public abstract class Quality {

    private String name;

    public Quality(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract String getLink();
    public abstract boolean isResolved();
}