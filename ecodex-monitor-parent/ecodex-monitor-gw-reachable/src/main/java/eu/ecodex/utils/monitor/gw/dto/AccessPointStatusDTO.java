/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.gw.dto;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.ToString;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.ProtocolVersion;

/**
 * Data Transfer Object representing the status of an access point.
 */
@Data
@ToString
public class AccessPointStatusDTO {
    /**
     * Name of the endpoint.
     */
    String name;
    /**
     * URL of the endpoint eg. service.example.com/domibus/services/msh.
     */
    String endpoint;
    ProtocolVersion[] allowedTls;
    ProtocolVersion usedTls;
    String[] localCertificates;
    String[] serverCertificates;
    List<CheckResultDTO> failures = new ArrayList<>();
    List<CheckResultDTO> warnings = new ArrayList<>();
    ZonedDateTime checkTime;
    HttpHost proxyHost;
    HttpHost targetHost;
}
