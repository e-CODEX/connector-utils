/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.keystores.config;

import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import lombok.Data;

/**
 * NamedKeyTrustStore extends StoreConfigurationProperties to provide additional properties and
 * functionalities specific to named key or trust stores.
 */
@Data
public class NamedKeyTrustStore extends StoreConfigurationProperties {
    /**
     * name of the key or truststore is used by the checks to reference this store.
     */
    String name;
    /**
     * Which store information should be exposed set to null or empty String if none.
     */
    String metricExposed = "*";
    /**
     * Which entry should be exposed?
     *
     * <p>Use comma seperated lists or expression here...
     */
    String entryExposed = "*";
    /**
     * Which information about the entry should be exposed?.
     */
    String entryMetricExposed = "*";
}
