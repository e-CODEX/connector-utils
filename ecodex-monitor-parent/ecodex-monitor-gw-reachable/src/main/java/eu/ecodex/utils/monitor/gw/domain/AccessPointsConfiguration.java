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

import java.util.ArrayList;
import java.util.Collection;
import lombok.Data;

/**
 * Represents the configuration for access points, including both the remote access points and the
 * gateway's own access point.
 */
@Data
public class AccessPointsConfiguration {
    int id = -1;
    /**
     * The remote access points.
     */
    Collection<AccessPoint> remoteAccessPoints = new ArrayList<>();
    /**
     * The own gateway.
     */
    AccessPoint self;
}
