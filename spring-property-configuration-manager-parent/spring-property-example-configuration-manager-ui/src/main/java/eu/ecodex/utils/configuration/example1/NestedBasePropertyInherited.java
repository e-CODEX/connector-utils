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
import lombok.Data;

/**
 * An extended property class that inherits from NestedBaseProperty.
 *
 * <p>This class adds two additional properties with specified configuration labels:
 * - prop1: labeled as "prop1 label"
 * - prop2: labeled as "prop2 label"
 */
@Data
@ConfigurationDescription("A extended property class")
public class NestedBasePropertyInherited extends NestedBaseProperty {
    @ConfigurationLabel("prop1 label")
    private String prop1;
    @ConfigurationLabel("prop2 label")
    private String prop2;
}
