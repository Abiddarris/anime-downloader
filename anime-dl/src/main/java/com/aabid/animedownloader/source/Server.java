package com.aabid.animedownloader.source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {

    private String id;
    private String name;
    private boolean ready;
    private final List<Quality> qualities = new ArrayList<>();
    private Episode episode;

    public Server(String id, String name, boolean ready, List<Quality> qualities) {
        this.id = id;
        this.name = name;
        this.ready = ready;
        this.qualities.addAll(qualities);
    }

    void attach(Episode episode) {
        this.episode = episode;
    }

    public Episode getEpisode() {
        return episode;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isReady() {
        return ready;
    }

    public List<Quality> getQualities() {
        return Collections.unmodifiableList(qualities);
    }

    public void setQualities(List<Quality> qualities) {
        this.qualities.removeAll(this.qualities);
        this.qualities.addAll(qualities);
        this.ready = true;
    }

}
