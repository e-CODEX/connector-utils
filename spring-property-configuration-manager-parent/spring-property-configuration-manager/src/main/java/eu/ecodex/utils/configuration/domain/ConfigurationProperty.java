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
import org.springframework.core.style.ToStringCreator;
import org.springframework.lang.Nullable;

/**
 * Represents a configuration property within a system, encapsulating its name, description,
 * type, default values, and other metadata. Configuration properties are typically used in
 * applications for defining and managing settings.
 */
@Data
@SuppressWarnings("squid:S1135")
public class ConfigurationProperty {
    // TODO: replace with org.springframework.boot.context
    //  .properties.source.ConfigurationPropertyName
    private String propertyName;
    private String beanPropertyName;
    private String description;
    private String label;
    private Class type;
    /**
     * Holds the with org.springframework.boot.context.properties.bind.DefaultValue annotated
     * default value.
     */
    private String[] defaultValue;
    /**
     * The class this property is part of.
     */
    @Nullable
    private Class parentClass;

    @Override
    public String toString() {
        return new ToStringCreator(this)
            .append("propertyName", propertyName)
            .append("label", label)
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConfigurationProperty that)) {
            return false;
        }

        return propertyName != null
            ? propertyName.equals(that.propertyName)
            : that.propertyName == null;
    }

    @Override
    public int hashCode() {
        return propertyName != null ? propertyName.hashCode() : 0;
    }
}
