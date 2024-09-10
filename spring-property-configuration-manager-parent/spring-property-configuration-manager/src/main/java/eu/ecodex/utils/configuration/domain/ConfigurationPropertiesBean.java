/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.configuration.domain;

import lombok.Data;

/**
 * A bean class for holding configuration properties information. This class encapsulates details
 * such as the bean instance, the class type of the bean, the name of the bean, the property prefix
 * associated with the bean, a description, and a label related to the configuration properties.
 */
@Data
public class ConfigurationPropertiesBean {
    private Object bean;
    private Class beanClazz;
    private String beanName;
    private String propertyPrefix;
    private String description;
    private String label;
}
