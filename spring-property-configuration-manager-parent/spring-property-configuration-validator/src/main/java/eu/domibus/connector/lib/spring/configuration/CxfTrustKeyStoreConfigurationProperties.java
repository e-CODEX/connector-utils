/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.lib.spring.configuration;

import eu.domibus.connector.lib.spring.configuration.validation.CheckKeyIsLoadableFromKeyStore;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * Configuration properties for a CXF trust key store. Extends the base configuration properties to
 * include specific settings for managing trust store configurations along with key and primary key
 * store settings.
 */
@Data
@Valid
@CheckKeyIsLoadableFromKeyStore
public class CxfTrustKeyStoreConfigurationProperties
    extends KeyAndKeyStoreAndTrustStoreConfigurationProperties {
    @Valid
    @NotNull
    private String encryptAlias;
}
