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
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Conditional class that checks if certificate monitoring is enabled based on the application's
 * environment properties.
 *
 * <p>This class implements the Condition interface and overrides the matches method to determine
 * if the certificate monitoring feature should be enabled.
 */
public class ConditionalOnCertificatesCheckEnabled implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String enabled = context.getEnvironment().getProperty(
            CertificateConfigurationProperties.CERTIFICATE_MONITOR_PREFIX + ".enabled", "false");
        return "true".equalsIgnoreCase(enabled);
    }
}
