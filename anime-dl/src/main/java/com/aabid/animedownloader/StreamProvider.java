package com.aabid.animedownloader;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StreamProvider {

    @JsonProperty("qualities")
    public List<VideoStream> streams;
}
