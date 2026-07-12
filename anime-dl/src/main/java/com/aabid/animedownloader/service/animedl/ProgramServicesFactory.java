package com.aabid.animedownloader.service.animedl;

import java.util.function.Function;

/**
 * Factory interface for creating ProgramServices instances from ProgramConfiguration.
 * Used for dependency injection and service creation.
 */
@FunctionalInterface
public interface ProgramServicesFactory extends Function<ProgramConfiguration, ProgramServices> {
}