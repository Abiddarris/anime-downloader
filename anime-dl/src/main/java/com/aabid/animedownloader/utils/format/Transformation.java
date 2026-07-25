package com.aabid.animedownloader.utils.format;

import org.jspecify.annotations.NonNull;

interface Transformation {
    @NonNull
    String apply(@NonNull String value);
}
