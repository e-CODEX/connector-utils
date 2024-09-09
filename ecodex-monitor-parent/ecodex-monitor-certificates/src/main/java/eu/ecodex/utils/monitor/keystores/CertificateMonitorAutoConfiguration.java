/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.keystores;

import eu.ecodex.utils.monitor.keystores.config.CertificateConfigurationProperties;
import eu.ecodex.utils.monitor.keystores.service.CertificateHealthIndicator;
import eu.ecodex.utils.monitor.keystores.service.CertificatesEndpoint;
import eu.ecodex.utils.monitor.keystores.service.KeyService;
import eu.ecodex.utils.monitor.keystores.service.crtprocessor.X509CertificateToStoreEntryInfoProcessorImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Autoconfiguration for monitoring certificates. This class configures beans related to
 * certificate monitoring, including a health indicator, key service, and an endpoint for
 * certificates.
 *
 * <p>The configuration is conditional based on the property defined by
 * {@code CertificateConfigurationProperties.CERTIFICATE_MONITOR_PREFIX}. If the property is set to
 * "true", the beans will be registered.
 */
@Configuration
@ConditionalOnProperty(
    prefix = CertificateConfigurationProperties.CERTIFICATE_MONITOR_PREFIX, name = "enabled",
    havingValue = "true"
)
@EnableConfigurationProperties(CertificateConfigurationProperties.class)
@ComponentScan(basePackageClasses = X509CertificateToStoreEntryInfoProcessorImpl.class)
public class CertificateMonitorAutoConfiguration {
    @Bean
    CertificateHealthIndicator certificateHealthIndicator() {
        return new CertificateHealthIndicator();
    }

    @Bean
    KeyService keyService() {
        return new KeyService();
    }

    @Bean
    CertificatesEndpoint certificatesEndpoint() {
        return new CertificatesEndpoint();
    }
}
