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

import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.ecodex.utils.monitor.gw.config.GatewayMonitorConfigurationProperties;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation of a TrustStrategy that ensures the trust chain is completed with certificates
 * from a predefined trust store.
 *
 * <p>This class is autoconfigured as a Spring component and relies on the
 * GatewayMonitorConfigurationProperties for its configuration.
 */
@Component
@SuppressWarnings("squid:S1135")
public class TrustStoreCompleteChainTrustStrategy implements TrustStrategy {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(TrustStoreCompleteChainTrustStrategy.class);
    @Autowired
    GatewayMonitorConfigurationProperties gatewayMonitorConfigurationProperties;
    private KeyStore trustStore;
    private StoreConfigurationProperties trustStoreConfig;

    /**
     * Initializes the trust store and its configuration for the
     * TrustStoreCompleteChainTrustStrategy.
     *
     * <p>This method is executed after the object is constructed and dependencies are injected.
     * It loads the key store based on the TLS settings provided in the gateway monitor
     * configuration properties.
     */
    @PostConstruct
    public void init() {
        this.trustStore =
            gatewayMonitorConfigurationProperties.getTls().getTrustStore().loadKeyStore();
        this.trustStoreConfig = gatewayMonitorConfigurationProperties.getTls().getTrustStore();
    }

    @Override
    public boolean isTrusted(X509Certificate[] x509Certificates, String s)
        throws CertificateException {
        X509Certificate firstCertificate = x509Certificates[0];
        validateCertificate(firstCertificate);
        return false;
    }

    private void validateCertificate(X509Certificate crt) throws CertificateException {
        try {
            validateKeyChain(crt, trustStore);
        } catch (KeyStoreException | InvalidAlgorithmParameterException | NoSuchAlgorithmException
                 | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Validate keychain.
     *
     * @param client   is the client X509Certificate
     * @param keyStore containing all trusted certificate
     * @return true if validation until root certificate success, false otherwise
     * @throws KeyStoreException                  if the provided key store cannot be open
     * @throws CertificateException               {@link #validateKeyChain(X509Certificate,
     *                                            X509Certificate...)}
     * @throws InvalidAlgorithmParameterException {@link #validateKeyChain(X509Certificate,
     *                                            X509Certificate...)}
     * @throws NoSuchAlgorithmException           {@link #validateKeyChain(X509Certificate,
     *                                            X509Certificate...)}
     * @throws NoSuchProviderException            {@link #validateKeyChain(X509Certificate,
     *                                            X509Certificate...)}
     */
    public boolean validateKeyChain(X509Certificate client, KeyStore keyStore)
        throws KeyStoreException, CertificateException, InvalidAlgorithmParameterException,
        NoSuchAlgorithmException, NoSuchProviderException {
        var certs = new X509Certificate[keyStore.size()];
        var i = 0;
        Enumeration<String> alias = keyStore.aliases();

        while (alias.hasMoreElements()) {
            certs[i++] = (X509Certificate) keyStore.getCertificate(alias
                                                                       .nextElement());
        }

        return validateKeyChain(client, certs);
    }

    /**
     * Validate keychain.
     *
     * @param client       is the client X509Certificate
     * @param trustedCerts is Array containing all trusted X509Certificate
     * @return true if validation until root certificate success, false otherwise
     * @throws CertificateException               thrown if the certificate is invalid
     * @throws InvalidAlgorithmParameterException @see
     *                                            {@link CertPathValidator#validate(CertPath,
     *                                            CertPathParameters)}
     * @throws NoSuchAlgorithmException           @see
     *                                            {@link CertPathValidator#validate(CertPath,
     *                                            CertPathParameters)}
     * @throws NoSuchProviderException            @see
     *                                            {@link CertPathValidator#validate(CertPath,
     *                                            CertPathParameters)}
     */
    public boolean validateKeyChain(X509Certificate client, X509Certificate... trustedCerts)
        throws CertificateException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
        NoSuchProviderException {
        var found = false;
        int i = trustedCerts.length;
        var certificateFactory = CertificateFactory.getInstance("X.509");
        TrustAnchor anchor;
        Set anchors;
        CertPath path;
        List list;
        PKIXParameters params;
        var validator = CertPathValidator.getInstance("PKIX");

        while (!found && i > 0) {
            anchor = new TrustAnchor(trustedCerts[--i], null);
            anchors = Collections.singleton(anchor);

            list = Arrays.asList(new Certificate[] {client});
            path = certificateFactory.generateCertPath(list);

            params = new PKIXParameters(anchors);
            params.setRevocationEnabled(false); // TODO: add config for revocation checks!

            X509Certificate currentCert = trustedCerts[i];
            if (client.getIssuerDN().equals(currentCert.getSubjectDN())) {
                try {
                    validator.validate(path, params);
                    if (isSelfSigned(currentCert)) {
                        found = true;
                        LOGGER.debug(
                            "validating root [{}]",
                            currentCert.getSubjectX500Principal().getName()
                        );
                    } else if (!client.equals(currentCert)) {
                        // find parent ca
                        LOGGER.debug(
                            "validating [{}] via: [{}] ",
                            client.getSubjectX500Principal().getName(),
                            currentCert.getSubjectX500Principal().getName()
                        );
                        found = validateKeyChain(currentCert, trustedCerts);
                    }
                } catch (CertPathValidatorException e) {
                    LOGGER.trace(
                        "validation fail, check next certificate in the trustedCerts array"
                    );
                }
            }
        }
        return found;
    }

    /**
     * Determines if the given X509Certificate is self-signed.
     *
     * @param cert is X509Certificate that will be tested
     * @return true if cert is self-signed, false otherwise
     * @throws CertificateException     if the certificate is invalid
     * @throws NoSuchAlgorithmException @see {@link X509Certificate#verify(PublicKey)}
     * @throws NoSuchProviderException  @see {@link X509Certificate#verify(PublicKey)}
     */
    public boolean isSelfSigned(X509Certificate cert)
        throws CertificateException, NoSuchAlgorithmException,
        NoSuchProviderException {
        try {
            var key = cert.getPublicKey();
            cert.verify(key);
            return true;
        } catch (SignatureException | InvalidKeyException sigEx) {
            return false;
        }
    }
}
