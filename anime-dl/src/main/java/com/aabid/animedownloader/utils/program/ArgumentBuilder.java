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
