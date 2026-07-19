/*
 * Copyright 2026 Aabid Darris
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aabid.animedownloader.service.anilist;

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
