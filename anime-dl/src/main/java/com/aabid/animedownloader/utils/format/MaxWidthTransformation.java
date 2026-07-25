package com.aabid.animedownloader.utils.format;

import org.jspecify.annotations.NonNull;

class MaxWidthTransformation extends WidthTransformation {

    private int maxWidth;

    public MaxWidthTransformation(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    @SuppressWarnings("null")
    @Override
    public @NonNull String apply(@NonNull String value) {
        if (value.length() > maxWidth) {
            return value.substring(0, maxWidth);
        }

        return value;
    }

}
