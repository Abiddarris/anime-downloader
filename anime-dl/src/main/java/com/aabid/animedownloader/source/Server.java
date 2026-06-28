package com.aabid.animedownloader.source;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;

public class Server {

    @NonNull
    private final ServerInfo info;

    @NonNull
    private final List<Quality> qualities;

    public Server(@NonNull ServerInfo info, @NonNull List<Quality> qualities) {
        this.info = info;
        this.qualities = qualities;
    }

    @NonNull
    public ServerInfo getInfo() {
        return info;
    }

    @NonNull
    public List<Quality> getQualities() {
        return Collections.unmodifiableList(qualities);
    }

    public Optional<Quality> getQuality(@NonNull String name) {
        for (Quality quality : qualities) {
            if (quality.getName().equals(name)) {
                return Optional.of(quality);
            }
        }

        return Optional.empty();
    }

}
