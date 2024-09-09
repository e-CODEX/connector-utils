/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.keystores.service.crtprocessor;

import eu.ecodex.utils.monitor.keystores.CertificateToStoreEntryInfoProcessor;
import eu.ecodex.utils.monitor.keystores.dto.StoreEntryInfo;
import java.io.IOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementation of the CertificateToStoreEntryInfoProcessor interface that processes X.509
 * certificates and fills in details for StoreEntryInfo.
 */
@Component
public class X509CertificateToStoreEntryInfoProcessorImpl
    implements CertificateToStoreEntryInfoProcessor {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(X509CertificateToStoreEntryInfoProcessorImpl.class);
    public static final String X509CertName = "X.509";

    @Override
    public boolean canProcess(String crtType) {
        return X509CertName.equals(crtType);
    }

    @Override
    public StoreEntryInfo processCrt(StoreEntryInfo info, byte[] crt) {
        try {
            var certificateHolder = new X509CertificateHolder(crt);
            info.setVersionNumber(certificateHolder.getVersionNumber());
            info.setIssuerName(certificateHolder.getIssuer().toString());
            info.setSubject(certificateHolder.getSubject().toString());
            info.setSerialNumber(certificateHolder.getSerialNumber());
            info.setNotAfter(certificateHolder.getNotAfter());
            info.setNotBefore(certificateHolder.getNotBefore());
        } catch (IOException e) {
            LOGGER.warn("Error while reading certificate", e);
        }

        return info;
    }
}

