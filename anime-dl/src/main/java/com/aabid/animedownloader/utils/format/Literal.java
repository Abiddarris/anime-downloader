package com.aabid.animedownloader.utils.format;

import java.util.Map;

import org.jspecify.annotations.NonNull;

class Literal implements Statement {

    private @NonNull String text;

    public Literal(@NonNull String text) {
        this.text = text;
    }

    @Override
    public String evaluate(Map<String, Object> values) {
        return text;
    }
}
