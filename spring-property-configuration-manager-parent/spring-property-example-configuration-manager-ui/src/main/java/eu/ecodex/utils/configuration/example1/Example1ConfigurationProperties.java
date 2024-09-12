/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.configuration.example1;

import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import eu.ecodex.utils.configuration.example1.validators.CompareStrings;
import eu.ecodex.utils.configuration.example1.validators.StringComparisonMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.nio.file.Path;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the Example1 module. The properties are prefixed with
 * "com.example1".
 */
@Data
@Component
@ConfigurationProperties(prefix = "com.example1")
@CompareStrings(
    propertyNames = {"password1", "password2"}, matchMode = StringComparisonMode.EQUAL,
    allowNull = true
)
public class Example1ConfigurationProperties {
    @ConfigurationLabel("Property1")
    @ConfigurationDescription("I am a good long description of this property!")
    String property1;
    @ConfigurationLabel("Property 2")
    @ConfigurationDescription("A 2nd property")
    String property2;
    @ConfigurationLabel("Password1")
    @ConfigurationDescription("A 2nd property")
    String password1;
    @ConfigurationLabel("Password2")
    @ConfigurationDescription("A 2nd property")
    String password2;
    @ConfigurationLabel("maxProperty")
    @ConfigurationDescription("A property")
    @Max(value = 200)
    int maxProperty;
    @ConfigurationLabel("minProperty")
    @ConfigurationDescription("A minProperty")
    @Min(value = 200)
    int minProperty;
    @ConfigurationLabel("A Path")
    @ConfigurationDescription("Path to anything")
    @NotNull
    Path thePath;
    @NestedConfigurationProperty
    NestedProperty subsetting = new NestedProperty();
    @NestedConfigurationProperty
    NestedBasePropertyInherited subset2 = new NestedBasePropertyInherited();
}
