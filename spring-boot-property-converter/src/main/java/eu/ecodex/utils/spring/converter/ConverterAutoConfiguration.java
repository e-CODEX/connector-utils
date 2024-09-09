/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.spring.converter;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up custom property converters in a Spring Boot application.
 *
 * <p>This class provides beans for converting String values to Path, Duration, and Resource
 * objects.
 */
@Configuration
public class ConverterAutoConfiguration {
    @Bean
    @ConfigurationPropertiesBinding
    public PathConverter stringToPathConverter() {
        return new PathConverter();
    }

    @Bean
    @ConfigurationPropertiesBinding
    public DurationConverter stringToDurationConverter() {
        return new DurationConverter();
    }

    @Bean
    @ConfigurationPropertiesBinding
    public ResourceConverter stringToResourceConverter() {
        return new ResourceConverter();
    }
}
