package com.aabid.animedownloader.source;

public class DirectQuality extends Quality {

    private String link;

    public DirectQuality(String name, String link) {
        super(name);

        this.link = link;
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
