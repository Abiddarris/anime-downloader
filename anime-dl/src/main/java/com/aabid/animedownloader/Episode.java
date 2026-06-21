package com.aabid.animedownloader;

public class Episode {

    private String sourceLink;
    public StreamProvider selectedProvider;

    public String getSourceLink() {
        return sourceLink;
    }

    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink;
    }
}
