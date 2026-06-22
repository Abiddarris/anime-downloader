package com.aabid.animedownloader.source;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StreamProvider {

    @JsonProperty("qualities")
    public List<VideoStream> streams;
}
