/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.configuration.service;

import eu.ecodex.utils.configuration.domain.ConfigurationPropertiesBean;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.boot.context.properties.bind.validation.ValidationBindHandler;
import org.springframework.boot.context.properties.bind.validation.ValidationErrors;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.validation.Validator;

/**
 * Implementation of the {@link ConfigurationPropertyChecker} interface responsible for checking the
 * configuration properties for validation errors. This class uses a {@link Validator} and a
 * {@link ConfigurationPropertyCollector} to validate configurations sourced from different
 * configurations.
 */
@Setter
@NoArgsConstructor
@SuppressWarnings("squid:S1135")
public class ConfigurationPropertyCheckerImpl implements ConfigurationPropertyChecker {
    private static final Logger LOGGER =
        LogManager.getLogger(ConfigurationPropertyCheckerImpl.class);
    @Autowired
    private Validator validator;
    @Autowired
    private ConfigurationPropertyCollector configurationPropertyCollector;

    public ConfigurationPropertyCheckerImpl(
        ConfigurationPropertyCollector configurationPropertyCollector, Validator validator) {
        this.configurationPropertyCollector = configurationPropertyCollector;
        this.validator = validator;
    }

    /**
     * Validates the configuration properties loaded from the given configuration source, filtering
     * the properties based on the provided base package classes.
     *
     * @param configurationPropertySource the property source which provides the properties to
     *                                    check
     * @param basePackageFilter           the filter under which all classes are located that need
     *                                    their properties to be validated
     * @return a list of validation errors encountered during the validation of the properties
     */
    public List<ValidationErrors> validateConfiguration(
        ConfigurationPropertySource configurationPropertySource, Class... basePackageFilter) {

        List<String> packageName = Arrays.stream(basePackageFilter)
                                         .map(Class::getPackage)
                                         .map(Package::getName)
                                         .toList();
        return validateConfiguration(configurationPropertySource, packageName);
    }

    public List<ValidationErrors> validateConfiguration(
        ConfigurationPropertySource configurationPropertySource, String... basePackageFilter) {
        return validateConfiguration(configurationPropertySource, Arrays.asList(basePackageFilter));
    }

    @Override
    public List<ValidationErrors> validateConfiguration(
        ConfigurationPropertySource configurationPropertySource,
        Collection<Class> configurationClasses) {
        LOGGER.debug("#isConfigurationValid for classes: [{}]", configurationClasses);

        return configurationClasses.stream()
                                   .map(
                                       entry -> this.isConfigValidForClazz(
                                           configurationPropertySource,
                                           entry
                                       ))
                                   .filter(Optional::isPresent)
                                   .map(Optional::get)
                                   .toList();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Tests if the configuration is valid, all properties are loaded from the provided
     * configuration source
     */
    public List<ValidationErrors> validateConfiguration(
        ConfigurationPropertySource configurationPropertySource, List<String> basePackageFilter) {
        LOGGER.debug("#isConfigurationValid for packages: [{}]", basePackageFilter);

        Collection<ConfigurationPropertiesBean> configurationBeans =
            configurationPropertyCollector.getConfigurationBeans(basePackageFilter);

        return configurationBeans.stream()
                                 .map(
                                     entry -> this.isConfigValidForClazz(
                                         configurationPropertySource,
                                         entry.getBeanClazz()
                                     ))
                                 .filter(Optional::isPresent)
                                 .map(Optional::get)
                                 .toList();
    }

    private Optional<ValidationErrors> isConfigValidForClazz(
        ConfigurationPropertySource configurationPropertySource, Class configClass) {

        ConfigurationProperties annotation =
            AnnotationUtils.getAnnotation(configClass, ConfigurationProperties.class);
        if (annotation == null) {
            return Optional.empty();
        }
        String prefix = (String) AnnotationUtils.getValue(annotation);
        if (prefix == null) {
            prefix = "";
        }

        Bindable<?> bindable = Bindable.of(configClass).withAnnotations(annotation);
        var binder = new Binder(configurationPropertySource);

        LOGGER.debug("Binding class [{}] with prefix [{}]", configClass, prefix);

        var validationBindHandler = new ValidationBindHandler(validator);

        try {
            binder.bind(prefix, bindable, validationBindHandler);
        } catch (BindValidationException bindValidationException) {
            return Optional.of(bindValidationException.getValidationErrors());
        } catch (BindException bindException) {
            Throwable cause = bindException.getCause();
            if (cause instanceof BindValidationException bindValidationException) {
                return Optional.of(bindValidationException.getValidationErrors());
            }
        }

        // TODO: validate bounded variables
        return Optional.empty();
    }
}
