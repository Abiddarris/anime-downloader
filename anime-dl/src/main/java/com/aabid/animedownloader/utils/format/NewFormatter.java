package com.aabid.animedownloader.utils.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NonNull;

public class NewFormatter {

    @NonNull
    private final String format;

    @NonNull
    private final List<String> names = new ArrayList<>();

    public NewFormatter(@NonNull String format) {
        StringBuilder formatBuilder = new StringBuilder();

        boolean insideBracket = false;
        int bracketStart = 0;
        int bracketEnd = -1;
        int argumentIndex = 1;
        for (int i = 0; i < format.length(); i++) {
            char c = format.charAt(i);
            if (!insideBracket && c == '{') {
                bracketStart = i;
                insideBracket = true;

                formatBuilder.append(format.substring(bracketEnd + 1, bracketStart));
                continue;
            } else if (insideBracket && c == '{') {
                throw new IllegalArgumentException("Illegal { character inside bracket");
            }

            if (insideBracket && c == '}') {
                String name = format.substring(bracketStart + 1, i);
                if (name.isEmpty()) {
                    throw new IllegalArgumentException(String.format("{%s} block should not be empty", name));
                }

                if (names.contains(name)) {
                    formatBuilder.append(String.format("%%%d$s", names.indexOf(name) + 1));
                } else {
                    formatBuilder.append(String.format("%%%d$s", argumentIndex++));
                }

                names.add(name);

                bracketEnd = i;
                insideBracket = false;
                continue;
            } else if (!insideBracket && c == '}') {
                throw new IllegalArgumentException("Illegal } character without opening bracket");
            }
        }

        if (insideBracket) {
            throw new IllegalArgumentException("Missing }");
        }

        formatBuilder.append(format.substring(bracketEnd + 1, format.length()));

        this.format = formatBuilder.toString();
    }

    @NonNull
    public String format(Map<String, Object> values) {
        Object[] args = new Object[names.size()];
        for (int i = 0; i < args.length; i++) {
            args[i] = values.get(names.get(i));
        }

        return String.format(format, args);
    }
}
