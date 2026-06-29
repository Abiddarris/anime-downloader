package com.aabid.animedownloader.cli;

import com.aabid.animedownloader.cli.output.OutputFormatter;

import picocli.CommandLine.ITypeConverter;

class OutputFormatterConverter implements ITypeConverter<OutputFormatter> {

    @Override
    public OutputFormatter convert(String value) throws Exception {
        return new OutputFormatter(value);
    }

}
