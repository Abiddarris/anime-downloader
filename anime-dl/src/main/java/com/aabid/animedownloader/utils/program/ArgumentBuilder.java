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
package com.aabid.animedownloader.utils.program;

import java.util.ArrayList;
import java.util.List;

public class ArgumentBuilder {

    private List<String> booleanOptions = new ArrayList<>();
    private List<Option> options = new ArrayList<>();
    private String[] positionalArgs;

    public ArgumentBuilder addBooleanOptions(String... options) {
        booleanOptions.addAll(List.of(options));
        return this;
    }

    public ArgumentBuilder setPositionalArguments(String... positionalArgs) {
        this.positionalArgs = positionalArgs;
        return this;
    }

    public ArgumentBuilder addOption(String optionName, String value) {
        options.add(new Option(optionName, value));
        return this;
    }

    public String[] build() {
        List<String> result = new ArrayList<>();
        result.addAll(booleanOptions);

        for (Option option : options) {
            result.add(option.getOptionName());
            result.add(option.getValue());
        }

        result.addAll(List.of(positionalArgs));
        return result.toArray(String[]::new);
    }

    private static class Option {

        private final String optionName;
        private final String value;

        public Option(String optionName, String value) {
            this.optionName = optionName;
            this.value = value;
        }

        public String getOptionName() {
            return optionName;
        }

        public String getValue() {
            return value;
        }

    }

}
