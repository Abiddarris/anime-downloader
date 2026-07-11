package com.aabid.animedownloader.cli.converter;

import com.aabid.animedownloader.utils.format.NewFormatter;

import picocli.CommandLine.ITypeConverter;

public class OutputFormatterConverter implements ITypeConverter<NewFormatter> {

    @Override
    public NewFormatter convert(String value) throws Exception {
        return new NewFormatter(value);
    }

}
