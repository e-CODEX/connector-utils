/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.keystores.service;

import eu.ecodex.utils.monitor.keystores.dto.StoreEntryInfo;
import eu.ecodex.utils.monitor.keystores.dto.StoreInfo;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;

/**
 * Endpoint providing operations related to certificates.
 *
 * <p>This class exposes operations to query information about certificate stores and their entries.
 */
@Endpoint(id = "certificates")
public class CertificatesEndpoint {
    @Autowired
    KeyService keyService;

    @ReadOperation
    public Map<String, StoreInfo> getStores() {
        return keyService.getStores();
    }

    @ReadOperation
    public StoreInfo getStoreEntryInfo(@Selector String storeName) {
        return keyService.getStoreEntryInfo(storeName);
    }

    @ReadOperation
    public StoreEntryInfo getStoreEntryInfo(
        @Selector String storeName, @Selector String aliasName) {
        return keyService.getStoreEntryInfo(null, storeName, aliasName);
    }
}
