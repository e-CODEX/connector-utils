/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.keystores.service;

import eu.ecodex.utils.monitor.keystores.config.CertificateConfigurationProperties;
import eu.ecodex.utils.monitor.keystores.config.KeyCheck;
import eu.ecodex.utils.monitor.keystores.dto.StoreEntryInfo;
import eu.ecodex.utils.monitor.keystores.service.crtprocessor.X509CertificateToStoreEntryInfoProcessorImpl;
import java.time.Instant;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

/**
 * Health indicator for monitoring the health of certificates. This class extends the
 * {@code AbstractHealthIndicator} and provides functionality to check the status of configured
 * certificate checks.
 */
@SuppressWarnings("squid:S1135")
public class CertificateHealthIndicator extends AbstractHealthIndicator {
    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateHealthIndicator.class);
    public static final String NOT_BEFORE_MESSAGE = "_not_before_message";
    public static final String NOT_AFTER_MESSAGE = "_not_after_message";
    public static final String NOT_AFTER_STATE = "_not_after_state";
    @Autowired
    CertificateConfigurationProperties crtCheckConfig;
    @Autowired
    KeyService keyService;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        builder.unknown();
        if (crtCheckConfig.getKeyChecks().isEmpty()) {
            builder.withDetail("message", "No key checks configured");
        }
        crtCheckConfig.getKeyChecks()
                      .forEach(check -> this.keyCheck(check, builder));
    }

    private void keyCheck(KeyCheck check, Health.Builder builder) {
        var alias = check.getAliasName();
        var storeName = check.getStoreName();
        var checkName = String.format("Check_%s@%s_%s", storeName, alias, check.getCheckName());
        var storeEntryInfo = keyService.getStoreEntryInfo("*", storeName, alias);

        if (!storeEntryInfo.getPresent()) {
            builder.unknown();
            builder.withDetail(
                checkName + "_message",
                String.format(
                    "No certificate found in store %s with alias %s",
                    check.getStoreName(), check.getAliasName()
                )
            );
            return;
        }
        builder.up();
        checkNotBefore(checkName, storeEntryInfo, builder);
        checkNotAfter(check, checkName, storeEntryInfo, builder);
        checkValidation(check, checkName, storeEntryInfo, builder);
    }

    private void checkValidation(
        KeyCheck check, String checkName, StoreEntryInfo storeEntryInfo, Health.Builder builder) {
        LOGGER.debug("#checkValidation: Checking certificate validation");

        if (X509CertificateToStoreEntryInfoProcessorImpl.X509CertName.equals(
            storeEntryInfo.getCertificateType())
            && storeEntryInfo.getCertificate() != null
        ) {
            // TODO: do validation check...

        }
    }

    private void checkNotAfter(
        KeyCheck check, String checkName, StoreEntryInfo storeEntryInfo, Health.Builder builder) {
        checkName = checkName + "_not_after";
        builder.withDetail(checkName + "_date", storeEntryInfo.getNotAfter());
        builder.withDetail(checkName + "_error_threshold", check.getErrorThreshold());

        if (storeEntryInfo.getNotAfter() == null) {
            builder.withDetail(
                checkName + NOT_AFTER_MESSAGE,
                "Was not able to check not before, because there is no not before information "
                    + "available"
            );
            return;
        }

        var notAfterWarn = storeEntryInfo.getNotAfter().toInstant();
        notAfterWarn.plus(check.getWarnThreshold());
        var notAfterError = storeEntryInfo.getNotAfter().toInstant();
        notAfterError.plus(check.getErrorThreshold());

        if (notAfterWarn.isBefore(Instant.now())) {
            LOGGER.warn("{}: Not after check is warn!", checkName);
            builder.withDetail(
                checkName + NOT_AFTER_MESSAGE,
                "Has failed because warn threshold for cert expiration has been reached"
            );
            builder.withDetail(checkName + NOT_AFTER_STATE, "warn");
            builder.down();
        } else {
            builder.withDetail(checkName + NOT_AFTER_MESSAGE, "Is ok");
            builder.withDetail(checkName + NOT_AFTER_STATE, "ok");
        }

        if (notAfterError.isBefore(Instant.now())) {
            LOGGER.error("{}: Not after check has failed!", checkName);
            builder.withDetail(
                checkName + NOT_AFTER_MESSAGE,
                "Has failed because error threshold for cert expiration has been reached"
            );
            builder.withDetail(checkName + NOT_AFTER_STATE, "failed");
            builder.down();
        } else {
            LOGGER.info("{}: Not after check is valid or warn!", checkName);
        }
    }

    private void checkNotBefore(
        String checkName, StoreEntryInfo storeEntryInfo, Health.Builder builder) {
        builder.withDetail(checkName + "_not_before_date", storeEntryInfo.getNotBefore());
        if (storeEntryInfo.getNotBefore() == null) {
            builder.withDetail(
                checkName + NOT_BEFORE_MESSAGE,
                "Was not able to check not before, because there is no not before information "
                    + "available"
            );
        } else if (storeEntryInfo.getNotBefore().before(new Date())) {
            LOGGER.info("{}: Not before check is valid!", checkName);
            builder.withDetail(checkName + NOT_BEFORE_MESSAGE, "Is ok");
        } else {
            LOGGER.info("{}: Not before check is failed!", checkName);
            builder.withDetail(checkName + NOT_BEFORE_MESSAGE, "Is failed");
            builder.down();
        }
    }
}
