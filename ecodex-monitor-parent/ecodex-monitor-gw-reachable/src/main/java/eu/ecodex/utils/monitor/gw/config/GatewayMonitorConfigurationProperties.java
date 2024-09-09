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

import eu.domibus.connector.lib.spring.configuration.TLSConnectionProperties;
import eu.ecodex.utils.monitor.gw.domain.AccessPointsConfiguration;
import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Gateway Monitor.
 *
 * <p>These properties are loaded from the application's configuration to configure how the gateway
 * monitoring should operate.
 */
@ConfigurationProperties(prefix = GatewayMonitorConfigurationProperties.GATEWAY_MONITOR_PREFIX)
@Data
public class GatewayMonitorConfigurationProperties {
    public static final String GATEWAY_MONITOR_PREFIX = "monitor.gw";
    /**
     * Configure how the Gateway config can be reached and accessed.
     */
    private GatewayRestInterfaceConfiguration rest;
    private AccessPointsConfiguration accessPoints;
    /**
     * This config holds the configuration for TLS - TLS client authentication, keystore + private
     * key for authentication - allowed/trusted TLS-servers.
     */
    private TLSConnectionProperties tls = new TLSConnectionProperties();
    private HealthCheckProperties healthCheck = new HealthCheckProperties();
    /**
     * How long should the last check result be cached?.
     */
    private Duration checkCacheTimeout = Duration.ofMinutes(5);
}
