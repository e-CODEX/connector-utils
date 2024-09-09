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
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;

/**
 * Endpoint to provide information about the reachability status of configured gateways.
 *
 * <p>This class provides operations to retrieve the status of all configured gateways as well as
 * the status of a specific gateway based on its name. The information is gathered by interacting
 * with the ConfiguredGatewaysService and GatewaysCheckerService.
 */
@Endpoint(id = "gateways")
public class GatewayReachableEndpoint {
    @Autowired
    ConfiguredGatewaysService configuredGatewaysService;
    @Autowired
    GatewaysCheckerService checkerService;

    @ReadOperation
    List<AccessPointStatusDTO> accessPointStatusList() {
        return configuredGatewaysService
            .getConfiguredGatewaysWithSelf()
            .stream()
            .map(ap -> checkerService.getGatewayStatus(ap))
            .toList();
    }

    /**
     * Retrieves the status information of a specific configured gateway access point.
     *
     * @param endpointName The name of the access point for which status information is requested.
     * @return An {@link AccessPointStatusDTO} object containing the status information of the
     *         specified access point. If the access point is not found, an empty
     *         {@link AccessPointStatusDTO} object is returned.
     */
    @ReadOperation
    public AccessPointStatusDTO getStoreEntryInfo(@Selector String endpointName) {
        var dto = new AccessPointStatusDTO();
        AccessPoint byName = configuredGatewaysService.getByName(endpointName);
        if (byName == null) {
            return dto;
        }
        return checkerService.getGatewayStatus(byName);
    }
}
