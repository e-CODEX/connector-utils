/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.configuration.ui.vaadin.tools;

import eu.ecodex.utils.configuration.ui.vaadin.tools.configfield.DefaultTextFieldFactory;
import eu.ecodex.utils.configuration.ui.vaadin.tools.configforms.ConfigurationFormsFactoryImpl;
import eu.ecodex.utils.configuration.ui.vaadin.tools.views.ListConfigurationPropertiesComponent;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

/**
 * ConfigurationPropertiesUiAutoConfiguration is a Spring configuration class that sets up the UI
 * components for managing configuration properties. It scans specific base packages to include
 * necessary components and provides a bean definition for a ConversionService that merges default
 * and UI-specific converters.
 */
@Configuration
@ComponentScan(
    basePackageClasses = {ListConfigurationPropertiesComponent.class, DefaultTextFieldFactory.class,
        ConfigurationFormsFactoryImpl.class}
)
public class ConfigurationPropertiesUiAutoConfiguration {
    @Autowired(required = false)
    @Qualifier(ConfigurationPropertiesBinding.VALUE)
    private Set<Converter<?, ?>> converters = new HashSet<>();
    @Autowired(required = false)
    @Qualifier(UiConfigurationPropertyConverter.VALUE)
    private Set<Converter<?, ?>> uiConfigurationConverters = new HashSet<>();

    /**
     * Creates a ConversionService bean that merges UI-specific and default converters.
     * This service will be used for the conversion of configuration properties in the UI layer.
     *
     * @return a ConversionService instance configured with a set of merged converters for both
     *         UI-specific and default configuration property conversions.
     */
    @Bean
    @UiConfigurationConversationService
    public ConversionService configurationPropertyUiConversionService() {
        Set<Converter<?, ?>> mergedConverters =
            Stream.of(uiConfigurationConverters.stream(), converters.stream())
                  .flatMap(Function.identity())
                  .collect(Collectors.toSet());

        var bean = new ConversionServiceFactoryBean();
        bean.setConverters(mergedConverters);
        bean.afterPropertiesSet();
        return bean.getObject();
    }
}
