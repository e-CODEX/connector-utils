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

import eu.domibus.ext.domain.PModeArchiveInfoDTO;
import eu.ecodex.configuration.pmode.Configuration;
import eu.ecodex.utils.monitor.gw.config.GatewayRestInterfaceConfiguration;
import eu.ecodex.utils.monitor.gw.domain.AccessPoint;
import eu.ecodex.utils.monitor.gw.domain.AccessPointsConfiguration;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.StringReader;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * PModeDownloader is responsible for downloading and updating PMode configurations from a specified
 * gateway REST interface. The class uses Spring's RestTemplate to interact with the REST API and
 * JAXB for XML parsing.
 */
public class PModeDownloader {
    private static final Logger LOGGER = LoggerFactory.getLogger(PModeDownloader.class);
    private final GatewayRestInterfaceConfiguration gatewayRestInterfaceConfiguration;
    private final RestTemplate restTemplate;
    private ConfigurationWrapper wrappedConfiguration = new ConfigurationWrapper();

    /**
     * Constructs a new PModeDownloader with the specified GatewayRestInterfaceConfiguration.
     *
     * @param gatewayRestInterfaceConfiguration the configuration settings for the Gateway REST
     *                                          interface.
     */
    public PModeDownloader(
        @Autowired GatewayRestInterfaceConfiguration gatewayRestInterfaceConfiguration) {
        this.gatewayRestInterfaceConfiguration = gatewayRestInterfaceConfiguration;
        this.restTemplate = new RestTemplateBuilder()
            .uriTemplateHandler(
                new DefaultUriBuilderFactory(gatewayRestInterfaceConfiguration.getUrl()))
            .basicAuthentication(
                gatewayRestInterfaceConfiguration.getUsername(),
                gatewayRestInterfaceConfiguration.getPassword()
            )
            .build();
    }

    public Configuration downloadPModes() {
        return downloadNewPModes(-1).config;
    }

    /**
     * Downloads the latest P-Modes if the current P-Mode ID is greater than the provided ID.
     *
     * @param id The ID of the current P-Mode.
     * @return The wrapped configuration object after attempting to download and unmarshal new
     *      P-Modes.
     */
    public ConfigurationWrapper downloadNewPModes(int id) {
        var lineSeparator = "\n\n####################";

        ResponseEntity<PModeArchiveInfoDTO> currentPMode =
            restTemplate.getForEntity("/ext/pmode/current", PModeArchiveInfoDTO.class);

        int pmodeId = -1;
        LOGGER.debug("Retrieved json [{}]", currentPMode);

        pmodeId = currentPMode.getBody().getId();

        if (pmodeId > id) {
            var forEntity = restTemplate.getForEntity("/ext/pmode/" + pmodeId, String.class);

            var xmlString = forEntity.getBody();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Downloaded pmode: {}", lineSeparator + xmlString + lineSeparator);
            }

            JAXBContext jaxbContext;
            try {
                jaxbContext = JAXBContext.newInstance(Configuration.class);

                var jaxbUnmarshaller = jaxbContext.createUnmarshaller();

                var configuration =
                    (Configuration) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
                this.wrappedConfiguration.config = configuration;
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace(
                        "Downloaded pmode: {}", lineSeparator + this.wrappedConfiguration
                            + lineSeparator
                    );
                }
            } catch (JAXBException e) {
                throw new RuntimeException(e);
            }
        }
        return this.wrappedConfiguration;
    }

    /**
     * Updates the access points configuration by downloading new P-Modes and setting the self and
     * remote access points.
     *
     * @param config The current access points configuration that needs to be updated. It must not
     *               be null.
     * @return The updated AccessPointsConfiguration object with the new settings.
     * @throws IllegalArgumentException if the provided config is null.
     */
    public AccessPointsConfiguration updateAccessPointsConfig(
        @NotNull AccessPointsConfiguration config) {
        if (config == null) {
            throw new IllegalArgumentException("Config is not allowed to be null!");
        }
        var wrappedConfig = this.downloadNewPModes(config.getId());
        var conf = wrappedConfig.config;
        String selfParty = conf.getParty();

        Map<String, AccessPoint> aps = conf
            .getBusinessProcesses()
            .getParties().getParty().stream()
            .map(this::mapMpc)
            .collect(Collectors.toMap(AccessPoint::getName, Function.identity()));

        AccessPoint self = aps.remove(selfParty);
        config.setSelf(self);
        config.setRemoteAccessPoints(aps.values());
        config.setId(wrappedConfig.id);
        return config;
    }

    private AccessPoint mapMpc(Configuration.BusinessProcesses.Parties.Party party) {
        var accessPoint = new AccessPoint();
        accessPoint.setName(party.getName());
        accessPoint.setEndpoint(party.getEndpoint());
        return accessPoint;
    }

    private static class ConfigurationWrapper {
        Configuration config;
        int id = -1;
    }
}



