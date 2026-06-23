package com.aabid.animedownloader.cli;

import picocli.CommandLine.IVersionProvider;

public class VersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() throws Exception {
        String version = getClass().getPackage().getImplementationVersion();
        return new String[] {version == null ? "dev" : version};
    }

}
