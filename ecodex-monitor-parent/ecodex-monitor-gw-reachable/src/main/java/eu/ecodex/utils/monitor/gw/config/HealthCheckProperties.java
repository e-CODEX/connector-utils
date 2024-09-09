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

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Properties for configuring health checks.
 *
 * <p>This class allows configuring whether the health check should include the current instance
 * and specifying a list of remote gateways to be checked.
 */
@Data
public class HealthCheckProperties {
    /**
     * should we check ourselves, by default true.
     */
    boolean checkSelf = true;
    /**
     * which remote gateways should be checked by health check? a * means all.
     */
    List<String> checkNames = new ArrayList<>();
}
