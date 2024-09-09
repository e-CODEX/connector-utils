/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.gw.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents an access point with a name and endpoint URL.
 */
@Data
@EqualsAndHashCode
public class AccessPoint {
    /**
     * Name of the endpoint.
     */
    @EqualsAndHashCode.Include
    String name;
    /**
     * URL of the endpoint eg. service.example.com/domibus/services/msh.
     */
    String endpoint;
}
