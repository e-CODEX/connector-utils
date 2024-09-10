/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.lib.spring.configuration;

import java.util.Properties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Abstract class representing the properties for configuring CXF certificate and key settings.
 */
@Data
public abstract class CxfCertKeyProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(CxfCertKeyProperties.class);
    public static final String ORG_APACHE_WSS_4_J_CRYPTO_MERLIN_KEYSTORE_FILE =
        "org.apache.wss4j.crypto.merlin.keystore.file";
    /**
     * This property configures the path to the security policy which should be used for the backend
     * webservice.
     *
     * <p>the default security policy requires signed and encrypted messages (body+header) the
     * signing and wss is done with certificates
     */
    @NestedConfigurationProperty
    private Resource wsPolicy = new ClassPathResource("/wsdl/backend.policy.xml");
    /**
     * Configuration of the privateKey store which is used to sign the transferred soap-messages and
     * decrypt the from the backendClient received messages.
     */
    @NestedConfigurationProperty
    private KeyAndKeyStoreConfigurationProperties privateKey;
    /**
     * Trust store which is used to verify the from the backendClient signed messages and encrypt
     * the messages transmitted to the backendClients.
     */
    @NestedConfigurationProperty
    private CertAndStoreConfigurationProperties trust;

    /**
     * Retrieves the Web Services Security (WSS) properties by mapping the certificate and key store
     * configuration properties to Merlin crypto properties.
     *
     * @return the configured WSS properties.
     */
    public Properties getWssProperties() {
        var properties = mapCertAndStoreConfigPropertiesToMerlinProperties();
        LOGGER.debug("getSignatureProperties() are: [{}]", properties);
        return properties;
    }

    /**
     * Maps the own configured properties to the crypto Properties also see
     * https://ws.apache.org/wss4j/config.html.
     *
     * @return the wss Properties
     */
    public Properties mapCertAndStoreConfigPropertiesToMerlinProperties() {
        var properties = new Properties();
        properties.setProperty(
            "org.apache.wss4j.crypto.provider", "org.apache.wss4j.common.crypto.Merlin");
        properties.setProperty("org.apache.wss4j.crypto.merlin.keystore.type", "jks");
        properties.setProperty(
            "org.apache.wss4j.crypto.merlin.keystore.password",
            this.getPrivateKey().getStore().getPassword()
        );
        LOGGER.debug(
            "setting [org.apache.wss4j.crypto.merlin.keystore.file={}]",
            this.getPrivateKey().getStore().getPath()
        );
        try {
            properties.setProperty(
                ORG_APACHE_WSS_4_J_CRYPTO_MERLIN_KEYSTORE_FILE,
                this.getPrivateKey().getStore().getPathUrlAsString()
            );
        } catch (Exception e) {
            throw new RuntimeException(
                "Error with property: [" + getPrefix() + ".privateKey.store.path]\n"
                    + "value is [" + this.getPrivateKey().getStore().getPath() + "]"
            );
        }
        properties.setProperty(
            "org.apache.wss4j.crypto.merlin.keystore.alias",
            this.getPrivateKey().getKey().getAlias()
        );
        properties.setProperty(
            "org.apache.wss4j.crypto.merlin.keystore.private.password",
            this.getPrivateKey().getKey().getPassword()
        );
        properties.setProperty(
            "org.apache.wss4j.crypto.merlin.truststore.password",
            this.getTrust().getStore().getPassword()
        );
        try {
            LOGGER.debug(
                "setting [org.apache.wss4j.crypto.merlin.truststore.file={}]",
                this.getTrust().getStore().getPath()
            );
            properties.setProperty(
                "org.apache.wss4j.crypto.merlin.truststore.file",
                this.getTrust().getStore().getPathUrlAsString()
            );
        } catch (Exception e) {
            LOGGER.info(
                "Trust Store Property: [{}.trust.store.path]"
                    + "\n cannot be processed. Using the configured privateKey store [{}] as "
                    + "trust store",
                getPrefix(), properties.getProperty(ORG_APACHE_WSS_4_J_CRYPTO_MERLIN_KEYSTORE_FILE)
            );

            properties.setProperty(
                "org.apache.wss4j.crypto.merlin.truststore.file",
                properties.getProperty(ORG_APACHE_WSS_4_J_CRYPTO_MERLIN_KEYSTORE_FILE)
            );
            properties.setProperty(
                "org.apache.wss4j.crypto.merlin.truststore.password",
                properties.getProperty("org.apache.wss4j.crypto.merlin.keystore.password")
            );
        }
        properties.setProperty(
            "org.apache.wss4j.crypto.merlin.load.cacerts",
            Boolean.toString(this.getTrust().isLoadCaCerts())
        );

        return properties;
    }

    private String getPrefix() {
        return "?not set?";
    }

    /**
     * KeyAndKeyStoreConfigurationProperties is a configuration properties class that encapsulates
     * the properties for configuring a key and a key store.
     */
    @Data
    public static class KeyAndKeyStoreConfigurationProperties {
        public KeyAndKeyStoreConfigurationProperties() {
        }

        public KeyAndKeyStoreConfigurationProperties(
            StoreConfigurationProperties keyStore, KeyConfigurationProperties key) {
            this.store = keyStore;
            this.key = key;
        }

        /**
         * Configuration of the (Key/Certificate)Store.
         */
        @NestedConfigurationProperty
        private StoreConfigurationProperties store;
        /**
         * Configures the default alias to use.
         */
        @NestedConfigurationProperty
        private KeyConfigurationProperties key;
    }

    /**
     * The CertAndStoreConfigurationProperties class is used to manage the configuration properties
     * for a certificate and key store, including whether to load system CA certificates.
     */
    @Data
    @NoArgsConstructor
    public static class CertAndStoreConfigurationProperties {
        public CertAndStoreConfigurationProperties(StoreConfigurationProperties keyStore) {
            this.store = keyStore;
        }

        /**
         * Configuration of the (Key/Certificate)Store.
         */
        @NestedConfigurationProperty
        private StoreConfigurationProperties store;
        /**
         * Load system Ca Certs? (default false).
         *
         * <p>Whether or not to load the CA certs in ${java.home}/lib/security/cacerts (default is
         * false)
         */
        private boolean loadCaCerts = false;
    }
}
