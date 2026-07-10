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
        } else if (cls == SearchCommand.class) {
            return cls.cast(new SearchCommand(factory));
        }

        return CommandLine.defaultFactory().create(cls);
    }

}
