/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.keystores.config;

import java.time.Duration;
import lombok.Data;
import org.springframework.lang.Nullable;

/**
 * Represents a configuration for checking keys within a keystore. This class holds various
 * properties that define how the key should be validated and monitored, including thresholds
 * for warnings and errors, as well as options for validation.
 */
@Data
public class KeyCheck {
    String checkName;
    String aliasName;
    String storeName;
    /**
     * If enabled it will be checked if the given alias is also a private key.
     */
    boolean shouldBePrivateKey = false;
    /**
     * The Health check should be warned, if the key expires in equal or less than warnThreshold
     * duration.
     *
     * <p>If null check should be omitted
     */
    @Nullable
    Duration warnThreshold = Duration.ofDays(60);
    /**
     * The Health check should fail, if the key expires in equal or less than errorThreshold
     * duration.
     *
     * <p>If null check should be omitted
     */
    @Nullable
    Duration errorThreshold = Duration.ofDays(30);
    /**
     * Should the certificate be validated?.
     */
    boolean enableValidation = true;
    /**
     * Should the default java trust store also be used to validate certificate? usually this store
     * is located under $JAVA_HOME/jre/lib/security/cacerts and can be set by
     * -Djavax.net.ssl.trustStore=...
     */
    boolean useSystemTrustStore = true;
    boolean useCrl = true;
    boolean useOcsp = true;
}
