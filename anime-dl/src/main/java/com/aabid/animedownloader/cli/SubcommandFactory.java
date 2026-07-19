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
package com.aabid.animedownloader.cli;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.service.animedl.ProgramServicesFactory;

import picocli.CommandLine;
import picocli.CommandLine.IFactory;

public class SubcommandFactory implements IFactory {

    @NonNull
    private ProgramServicesFactory factory;

    public SubcommandFactory(@NonNull ProgramServicesFactory factory) {
        this.factory = factory;
    }

    @Override
    public <K> K create(Class<K> cls) throws Exception {
        if (cls == InfoSubcommand.class) {
            return cls.cast(new InfoSubcommand(factory));
        } else if (cls == DownloadSubcommand.class) {
            return cls.cast(new DownloadSubcommand(factory));
        } else if (cls == SearchSubcommand.class) {
            return cls.cast(new SearchSubcommand(factory));
        }

        return CommandLine.defaultFactory().create(cls);
    }

}
