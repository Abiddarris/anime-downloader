package com.aabid.animedownloader.source;

public class DirectQuality implements Quality {

    private String name;
    private String link;

    public DirectQuality(String name, String link) {
        this.name = name;
        this.link = link;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public boolean isResolved() {
        return true;
    }

}
