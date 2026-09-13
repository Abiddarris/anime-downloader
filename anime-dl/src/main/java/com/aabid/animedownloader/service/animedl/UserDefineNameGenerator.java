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
package com.aabid.animedownloader.service.animedl;

import java.util.Map;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.anime.EpisodeInfo;
import com.aabid.animedownloader.utils.format.NewFormatter;

/**
 * User-defined output filename generator that formats a map of runtime values.
 */
public class UserDefineNameGenerator implements OutputNameGenerator {

    @NonNull
    private final NewFormatter formatter;

    @NonNull
    private final ValuesProvider valuesProvider;

    public UserDefineNameGenerator(@NonNull NewFormatter formatter, @NonNull ValuesProvider valuesProvider) {
        this.formatter = formatter;
        this.valuesProvider = valuesProvider;
    }

    @Override
    @NonNull
    public String generate(@NonNull EpisodeInfo info, @NonNull Selection selection) {
        Map<String, Object> values = valuesProvider.getValues(info, selection);
        return formatter.format(values);
    }
}
