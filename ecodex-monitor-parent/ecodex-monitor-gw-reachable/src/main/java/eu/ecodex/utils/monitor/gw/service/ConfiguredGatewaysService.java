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

import static eu.ecodex.utils.monitor.gw.config.GatewayMonitorConfigurationProperties.GATEWAY_MONITOR_PREFIX;

import eu.ecodex.utils.monitor.gw.config.GatewayMonitorConfigurationProperties;
import eu.ecodex.utils.monitor.gw.domain.AccessPoint;
import eu.ecodex.utils.monitor.gw.domain.AccessPointsConfiguration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * Service for managing and retrieving the configured gateways in the system.
 *
 * <p>The class coordinates with configuration properties and a PMode downloader to update and
 * provide access to the access points configuration.
 */
public class ConfiguredGatewaysService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfiguredGatewaysService.class);
    @Autowired
    private GatewayMonitorConfigurationProperties monitorConfigurationProperties;
    @SuppressWarnings("checkstyle:MemberName")
    @Autowired
    private PModeDownloader pModeDownloader;
    @Setter
    @Getter
    private AccessPointsConfiguration accessPointConfig = new AccessPointsConfiguration();

    /**
     * Updates the currently configured access points based on the configuration properties.
     *
     * @throws RuntimeException if no access points configuration is found in the properties and
     *                          loading from p-Modes is not enabled.
     */
    public synchronized void updateConfiguredGateways() {
        if (monitorConfigurationProperties.getRest().isLoadPmodes()) {
            this.accessPointConfig = pModeDownloader.updateAccessPointsConfig(accessPointConfig);
            LOGGER.info("Loaded configured access points from gateway p-Modes");
        } else if (monitorConfigurationProperties.getAccessPoints() != null) {
            this.accessPointConfig = monitorConfigurationProperties.getAccessPoints();
            LOGGER.info("Loaded configured access points from properties!");
        } else {
            throw new RuntimeException(
                "No access points are configured in properties under: "
                    + "\n[" + GATEWAY_MONITOR_PREFIX
                    + ".access-points] neither is "
                    + "\n loading config from p-modes enabled:"
                    + "\n[" + GATEWAY_MONITOR_PREFIX
                    + ".rest.load-pmodes] is false");
        }
    }

    public synchronized Collection<AccessPoint> getConfiguredGateways() {
        updateConfiguredGateways();
        return this.accessPointConfig.getRemoteAccessPoints();
    }

    /**
     * Retrieves the collection of all configured gateways, including the gateway's own access
     * point. The method first updates the list of configured gateways and then adds the gateway's
     * own access point to the collection before returning it.
     *
     * @return A collection of {@link AccessPoint} instances representing the configured gateways,
     *      including the gateway's own access point.
     */
    public synchronized Collection<AccessPoint> getConfiguredGatewaysWithSelf() {
        updateConfiguredGateways();
        List<AccessPoint> list = new ArrayList<>();
        list.add(getSelf());
        list.addAll(this.accessPointConfig.getRemoteAccessPoints());
        return list;
    }

    public synchronized AccessPoint getSelf() {
        updateConfiguredGateways();
        return this.accessPointConfig.getSelf();
    }

    /**
     * Retrieves an AccessPoint by its name from the configured gateways.
     *
     * @param name The name of the access point to be retrieved. Must not be empty.
     * @return The AccessPoint with the specified name, or null if no such access point is found.
     * @throws IllegalArgumentException if the provided name is empty.
     */
    public synchronized AccessPoint getByName(String name) {
        updateConfiguredGateways();
        if (!StringUtils.hasLength(name)) {
            throw new IllegalArgumentException("Name is not allowed to be empty!");
        }
        if (name.equals(this.accessPointConfig.getSelf().getName())) {
            return this.accessPointConfig.getSelf();
        }
        Optional<AccessPoint> first = this.accessPointConfig.getRemoteAccessPoints()
                                                            .stream()
                                                            .filter(ap -> name.equals(ap.getName()))
                                                            .findFirst();

        return first.orElse(null);
    }
}
