package com.aabid.animedownloader.service.animedl;

import java.util.function.Function;

@FunctionalInterface
public interface ProgramServicesFactory extends Function<ProgramConfiguration, ProgramServices> {
}
