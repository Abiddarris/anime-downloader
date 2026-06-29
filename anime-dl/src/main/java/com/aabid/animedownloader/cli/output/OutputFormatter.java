package com.aabid.animedownloader.cli.output;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;

public class OutputFormatter {

    private static final Pattern OUTPUT_FORMAT_PATTERN = Pattern.compile("(\\{.+?\\})");

    @NonNull
    private String format;

    @NonNull
    private List<String> names = new ArrayList<>();

    public OutputFormatter(@NonNull String format) {
        Matcher matcher = OUTPUT_FORMAT_PATTERN.matcher(format);
        StringBuilder formatBuilder = new StringBuilder();
        int normalTextStart = 0;
        int argumentIndex = 1;
        while (matcher.find()) {
            String name = format.substring(matcher.start() + 1, matcher.end() - 1);

            formatBuilder.append(format.substring(normalTextStart, matcher.start()));

            if (names.contains(name)) {
                formatBuilder.append(String.format("%%%d$s", names.indexOf(name) + 1));
            } else {
                formatBuilder.append(String.format("%%%d$s", argumentIndex++));
            }

            names.add(name);

            normalTextStart = matcher.end();
        }

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
