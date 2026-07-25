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
package com.aabid.animedownloader.utils.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

class Variable implements Statement {

    @NonNull
    private final String variableName;

    @NonNull
    private final List<Transformation> transformations = new ArrayList<>();

    @SuppressWarnings("null")
    public Variable(String variableName) {
        String[] components = variableName.split(":");
        this.variableName = components[0];

        for (int i = 1; i < components.length; i++) {
            if (components[i].equals("upper")) {
                transformations.add(CaseTransformation.UPPER);
            } else if (components[i].equals("lower")) {
                transformations.add(CaseTransformation.LOWER);
            } else {
                throw new IllegalArgumentException("Unknown specifier: " + components[i]);
            }
        }

        if (transformations.stream()
            .filter(t -> t instanceof CaseTransformation)
            .count() > 1) {
            throw new IllegalArgumentException("upper and lower are mutually exclusive");
        }
    }

    @SuppressWarnings("null")
    @Override
    public String evaluate(Map<String, Object> values) {
        String value = Objects.toString(values.get(variableName));
        for (Transformation transformation : transformations) {
            value = transformation.apply(value);
        }
        return value;
    }

}
