/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.configuration.spring;

import eu.ecodex.utils.configuration.service.ConfigurationPropertyChecker;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyCheckerImpl;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyCollectorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for managing configuration properties.
 * This class contains the necessary bean definitions for handling configuration properties
 * and checking their validity.
 */
@Configuration
public class ConfigurationPropertyManagerConfiguration {
    @Bean
    public ConfigurationPropertyCollectorImpl configurationPropertyManagerImpl() {
        return new ConfigurationPropertyCollectorImpl();
    }

    @Bean
    public ConfigurationPropertyChecker configurationPropertyChecker() {
        return new ConfigurationPropertyCheckerImpl();
    }
}
