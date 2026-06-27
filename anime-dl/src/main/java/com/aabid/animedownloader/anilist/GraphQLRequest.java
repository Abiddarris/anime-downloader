package com.aabid.animedownloader.anilist;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.NonNull;

class GraphQLRequest {

    @NonNull
    private String query;

    @NonNull
    private Map<String, Object> variables = new HashMap<>();

    public GraphQLRequest(@NonNull String query) {
        this.query = query;
    }

    public void setVariable(@NonNull String name, @NonNull Object value) {
        variables.put(name, value);
    }

    public String getQuery() {
        return query;
    }

    public Map<String, Object> getVariables() {
        return Collections.unmodifiableMap(variables);
    }
}
