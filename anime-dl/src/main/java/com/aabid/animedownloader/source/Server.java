package com.aabid.animedownloader.source;

import java.util.Collections;
import java.util.List;

public class Server {

    private String id;
    private String name;
    private boolean ready;
    private List<Quality> qualities;

    public Server(String id, String name, boolean ready, List<Quality> qualities) {
        this.id = id;
        this.name = name;
        this.ready = ready;
        this.qualities = qualities;
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

}
