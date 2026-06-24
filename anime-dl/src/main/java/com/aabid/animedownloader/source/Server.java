package com.aabid.animedownloader.source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;

public class Server {

    @NonNull
    private Metadata metadata;

    @NonNull
    private String id;

    @NonNull
    private String name;

    private boolean ready;

    private final List<Quality> qualities = new ArrayList<>();

    public Server(@NonNull Metadata metadata, @NonNull String id,
                  @NonNull String name, boolean ready, @NonNull List<@NonNull Quality> qualities) {
        this.metadata = metadata;
        this.id = id;
        this.name = name;
        this.ready = ready;
        this.qualities.addAll(qualities);
    }

    @NonNull
    public Metadata getMetadata() {
        return metadata;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public boolean isReady() {
        return ready;
    }

    public Optional<Quality> getQuality(@NonNull String name) {
        for (Quality quality : qualities) {
            if (quality.getName().equals(name)) {
                return Optional.of(quality);
            }
        }

        return Optional.empty();
    }

    public List<Quality> getQualities() {
        return Collections.unmodifiableList(qualities);
    }

    @Override
    public String toString() {
        return name;
    }

    public void setQualities(@NonNull List<Quality> qualities) {
        this.qualities.removeAll(this.qualities);
        this.qualities.addAll(qualities);
        this.ready = true;
    }

}
