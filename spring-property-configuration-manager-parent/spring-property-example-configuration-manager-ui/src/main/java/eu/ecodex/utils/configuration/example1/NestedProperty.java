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
import java.time.Duration;
import lombok.Data;

/**
 * Represents a nested property configuration.
 */
@Data
@ConfigurationDescription("A nested property class")
public class NestedProperty {
    @ConfigurationLabel("Testprop")
    String test;
    @ConfigurationLabel("Duration property")
    @ConfigurationDescription("The duration for something")
    Duration duration;
}
