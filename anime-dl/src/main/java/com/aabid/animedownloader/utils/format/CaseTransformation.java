package com.aabid.animedownloader.utils.format;

import org.jspecify.annotations.NonNull;

enum CaseTransformation implements Transformation {

    @SuppressWarnings("null")
    UPPER(String::toUpperCase),

    @SuppressWarnings("null")
    LOWER(String::toLowerCase);

    private @NonNull Transformation transformation;

    private CaseTransformation(@NonNull Transformation transformation) {
        this.transformation = transformation;
    }

    @Override
    public @NonNull String apply(@NonNull String value) {
        return transformation.apply(value);
    }

}
