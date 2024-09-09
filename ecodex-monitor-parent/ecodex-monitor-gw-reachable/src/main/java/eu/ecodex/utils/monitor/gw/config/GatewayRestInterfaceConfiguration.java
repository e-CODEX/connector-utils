/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.gw.config;

import lombok.Data;

/**
 * Holds configuration settings for the Gateway REST interface.
 */
@Data
public class GatewayRestInterfaceConfiguration {
    boolean loadPmodes = true;
    /**
     * The URL of the gateway.
     */
    private String url;
    /**
     * A gateway user which has the rights to at least download the current p-mode set.
     */
    private String username;
    /**
     * The password for the gateway user.
     */
    private String password;
}
