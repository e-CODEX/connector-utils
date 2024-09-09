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

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Configuration properties for monitoring certificates. This class holds the configuration specific
 * to certificate monitoring, including enabling or disabling the monitoring feature and defining
 * the certificate stores and checks to be used.
 */
@Data
@ConfigurationProperties(prefix = CertificateConfigurationProperties.CERTIFICATE_MONITOR_PREFIX)
public class CertificateConfigurationProperties {
    public static final String CERTIFICATE_MONITOR_PREFIX = "monitor.certificates";
    private boolean enabled = false;
    @NestedConfigurationProperty
    List<NamedKeyTrustStore> stores = new ArrayList<>();
    @NestedConfigurationProperty
    List<KeyCheck> keyChecks = new ArrayList<>();
}
