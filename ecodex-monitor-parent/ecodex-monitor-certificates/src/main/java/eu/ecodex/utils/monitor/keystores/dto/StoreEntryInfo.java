/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.keystores.dto;

import java.math.BigInteger;
import java.util.Date;
import lombok.Data;

/**
 * Class representing information about an entry in a store.
 */
@Data
@SuppressWarnings("squid:S1135")
public class StoreEntryInfo {
    String aliasName;
    String certificateType;
    int versionNumber;
    String issuerName;
    String subject;
    BigInteger serialNumber;
    Date notAfter;
    Date notBefore;
    Boolean present = false;
    byte[] certificate;
    // TODO: certificate attributes
}
