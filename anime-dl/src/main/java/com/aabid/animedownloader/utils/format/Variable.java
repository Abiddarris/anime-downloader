package com.aabid.animedownloader.utils.format;

import java.util.Map;
import java.util.Objects;

class Variable implements Statement {

    private String variableName;

    public Variable(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public String evaluate(Map<String, Object> values) {
        return Objects.toString(values.get(variableName));
    }

}
