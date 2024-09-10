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
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.domain.ConfigurationPropertyNode;
import java.util.Collection;
import java.util.List;

/**
 * The ConfigurationPropertyCollector interface defines methods for collecting and retrieving
 * configuration properties within a specified base package or class. It provides functionality for
 * obtaining list and hierarchical representations of configuration properties.
 */
public interface ConfigurationPropertyCollector {
    /**
     * Returns a list of all Properties (within with
     * {@link org.springframework.boot.context.properties.ConfigurationProperties} annotated
     * Classes) in the provided basePackage path.
     *
     * @param basePackage the base packages to scan for configuration properties
     * @return the list of Properties
     */
    Collection<ConfigurationProperty> getConfigurationProperties(String... basePackage);

    Collection<ConfigurationProperty> getConfigurationProperties(Class... basePackageClasses);

    ConfigurationPropertyNode getConfigurationPropertiesHirachie(String... basePackage);

    Collection<ConfigurationProperty> getConfigurationPropertyFromClazz(Class<?> beanClass);

    Collection<ConfigurationPropertiesBean> getConfigurationBeans(List<String> basePackageFilter);
}
