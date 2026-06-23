package com.aabid.animedownloader.cli;

import com.aabid.animedownloader.source.AnimeSource;

import picocli.CommandLine;
import picocli.CommandLine.IFactory;

public class SubcommandFactory implements IFactory {

    private AnimeSource source;

    public SubcommandFactory(AnimeSource source) {
        this.source = source;
    }

    @Override
    public <K> K create(Class<K> cls) throws Exception {
        if (cls == InfoSubcommand.class) {
            return cls.cast(new InfoSubcommand(source));
        }

        return CommandLine.defaultFactory().create(cls);
    }

}
