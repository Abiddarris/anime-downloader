package com.aabid.animedownloader.utils.format;

import org.jspecify.annotations.NonNull;

import com.google.common.base.Strings;

class JustifyTransformation extends WidthTransformation {

    private int length;
    private @NonNull Justify justify;

    public JustifyTransformation(int length, @NonNull Justify justify) {
        this.length = length;
        this.justify = justify;
    }


    @SuppressWarnings("null")
    @Override
    public @NonNull String apply(@NonNull String value) {
        return switch (justify) {
            case LEFT -> Strings.padEnd(new MaxWidthTransformation(length).apply(value), length, ' ');
            case RIGHT -> {
                if (value.length() > length) {
                    yield value.substring(value.length() - length);
                }
                yield Strings.padStart(value, length, ' ');
            }
        };
    }

    static enum Justify {
        LEFT, RIGHT
    }

}
