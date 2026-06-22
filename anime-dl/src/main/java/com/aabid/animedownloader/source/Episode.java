package com.aabid.animedownloader.source;

import java.util.Collections;
import java.util.List;

public class Episode {

    private String sourceLink;
    private List<Server> servers;

    public Episode(String sourceLink, List<Server> servers) {
        this.sourceLink = sourceLink;
        this.servers = servers;
    }

    public List<Server> getServers() {
        return Collections.unmodifiableList(servers);
    }

    public String getSourceLink() {
        return sourceLink;
    }

}
