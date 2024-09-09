/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.gw.service;

import eu.ecodex.utils.monitor.gw.domain.AccessPoint;
import eu.ecodex.utils.monitor.gw.dto.AccessPointStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

/**
 * GatewayHealthIndicator is an implementation of the AbstractHealthIndicator that provides health
 * checks specifically for gateway services.
 */
public class GatewayHealthIndicator extends AbstractHealthIndicator {
    @Autowired
    ConfiguredGatewaysService configuredGatewaysService;
    @Autowired
    GatewaysCheckerService gwChecker;

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        builder.up();
        checkSelf(builder);
    }

    private void checkSelf(Health.Builder builder) {
        AccessPoint ap = configuredGatewaysService.getSelf();
        AccessPointStatusDTO gatewayStatus = gwChecker.getGatewayStatus(ap);

        if (!gatewayStatus.getFailures().isEmpty()) {
            builder.down();
            builder.withDetail("self_detail", gatewayStatus.getFailures().getFirst().toString());
        }
    }
}
