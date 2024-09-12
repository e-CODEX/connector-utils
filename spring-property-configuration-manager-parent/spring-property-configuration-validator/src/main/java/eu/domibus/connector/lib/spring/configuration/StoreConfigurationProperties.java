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

import eu.domibus.connector.lib.spring.configuration.validation.CheckResourceIsReadable;
import eu.domibus.connector.lib.spring.configuration.validation.CheckStoreIsLoadable;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * StoreConfigurationProperties is a class that encapsulates the configuration properties for a
 * key/trust store, including the path, password, and type of the store. This class also provides
 * methods to validate and interact with the store.
 */
@Data
@NoArgsConstructor
@CheckStoreIsLoadable
@SuppressWarnings("squid:S1135")
public class StoreConfigurationProperties {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(StoreConfigurationProperties.class);
    /**
     * Path to the Key/Truststore.
     */
    @CheckResourceIsReadable
    private Resource path;
    /**
     * Password to open the Store.
     */
    @NotNull
    private String password;
    /**
     * Type of the java key store.
     */
    @NotNull
    private String type = "JKS";

    public StoreConfigurationProperties(Resource path, String password) {
        this.path = path;
        this.password = password;
    }

    /**
     * Converts the path URL to a string representation.
     *
     * @return The string representation of the path URL if the path is not null; null otherwise.
     * @throws UncheckedIOException if there is an error during URL retrieval.
     */
    public String getPathUrlAsString() {
        try {
            if (path == null) {
                LOGGER.debug("#getPathUrlAsString: resolved to null");
                return null;
            }
            LOGGER.trace(
                "#getPathUrlAsString: get url from [{}] to [{}]", path, path.getURL());
            return path.getURL().toString();
        } catch (IOException e) {
            throw new UncheckedIOException("#getPathUrlAsString: path: [" + path + "]", e);
        }
    }

    /**
     * Validates if the path resource is readable.
     *
     * @throws ValidationException if the path is null, the InputStream is null, or if an
     *                             IOException occurs while trying to open the InputStream.
     */
    public void validatePathReadable() {
        if (getPath() == null) {
            throw new ValidationException("Path is null!");
        }
        try {
            var inputStream = this.getPath().getInputStream();
            if (inputStream == null) {
                throw new ValidationException("Input Stream from path is null!");
            }
            inputStream.close();
        } catch (IOException e) {
            throw new ValidationException("IOException occured during open", e);
        }
    }

    /**
     * Validates if the path resource is writable.
     *
     * @throws ValidationException if the path is null, the path is not writable, an IOException
     *                             occurs during validation, or an IllegalArgumentException occurs
     *                             during validation.
     */
    public void validatePathWriteable() {
        if (getPath() == null) {
            throw new ValidationException("Path is null!");
        }
        try {
            if (Files.isWritable(Paths.get(getPath().getURI()))) {
                // TODO check why this section is empty
            }
            throw new ValidationException("Path not writeable!");
        } catch (IOException e) {
            throw new ValidationException("IOException occured during open", e);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("IllegalArgumentException occured during open", e);
        }
    }

    /**
     * Validates if a key associated with the provided alias exists in the key store.
     *
     * @param alias    the alias of the key to check for existence
     * @param password the password used to access the key store
     * @throws ValidationException if the key store cannot be accessed, or the key is not found
     */
    public void validateKeyExists(String alias, String password) {
        KeyStore keyStore;
        keyStore = loadKeyStore();

        try {
            var key = keyStore.getKey(alias, password.toCharArray());
            if (key == null) {
                throw new ValidationException(String.format("No key found for alias [%s]", alias));
            }
        } catch (KeyStoreException e) {
            throw new ValidationException(
                String.format("Key Store exception when retrieving key alias [%s]", alias), e);
        } catch (NoSuchAlgorithmException e) {
            throw new ValidationException(
                String.format("No such key exception when retrieving key alias [%s]", alias), e);
        } catch (UnrecoverableKeyException e) {
            throw new ValidationException(
                String.format("Validation exception when retrieving key alias [%s]", alias), e);
        }
    }

    /**
     * Validates if a certificate associated with the provided alias exists in the key store.
     *
     * @param alias the alias of the certificate to check for existence
     * @throws ValidationException if the certificate is not found or if there is an error accessing
     *                             the key store
     */
    public void validateCertExists(String alias) {
        KeyStore keyStore;
        keyStore = loadKeyStore();
        try {
            var certificate = keyStore.getCertificate(alias);
            if (certificate == null) {
                throw new ValidationException(
                    String.format("No certificate found for alias [%s]", alias)
                );
            }
        } catch (KeyStoreException e) {
            throw new ValidationException(String.format(
                "Key store exception occured while loading certificate with alias [%s] from key "
                    + "store",
                alias
            ), e);
        }
    }

    /**
     * Loads and returns a KeyStore instance based on the provided path and password. Validates the
     * path readability before attempting to load the KeyStore. Throws CannotLoadKeyStoreException
     * if the KeyStore cannot be loaded due to validation failure or other IO errors.
     *
     * @return the loaded KeyStore instance
     * @throws CannotLoadKeyStoreException if the KeyStore cannot be loaded
     */
    public KeyStore loadKeyStore() {
        try {
            validatePathReadable();
        } catch (ValidationException ve) {
            throw new CannotLoadKeyStoreException(
                String.format("Cannot load key store from path %s", getPath()), ve);
        }
        if (password == null) {
            password = "";
        }
        char[] pwdArray = password.toCharArray();
        try (InputStream inputStream = getPath().getInputStream()) {
            var keyStore = KeyStore.getInstance(this.type);
            keyStore.load(inputStream, pwdArray);
            return keyStore;
        } catch (IOException | KeyStoreException | CertificateException
                 | NoSuchAlgorithmException e) {
            throw new CannotLoadKeyStoreException(
                String.format("Cannot load key store from path %s", getPath()), e);
        }
    }

    /**
     * Exception thrown when a KeyStore cannot be loaded.
     *
     * <p>This is a type of RuntimeException that indicates failure to load a KeyStore, often used
     * when validating the readability or availability of key store path or when there are IO errors
     * during the loading process.
     */
    @NoArgsConstructor
    public static class CannotLoadKeyStoreException extends RuntimeException {
        public CannotLoadKeyStoreException(String message) {
            super(message);
        }

        public CannotLoadKeyStoreException(String message, Throwable cause) {
            super(message, cause);
        }

        public CannotLoadKeyStoreException(Throwable cause) {
            super(cause);
        }

        public CannotLoadKeyStoreException(
            String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    /**
     * A custom exception used to indicate validation errors within the system.
     *
     * <p>This exception typically signals issues encountered during validation of resources,
     * paths, or configurations, and helps in distinguishing validation errors from other types of
     * runtime exceptions.
     */
    @NoArgsConstructor
    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }

        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }

        public ValidationException(Throwable cause) {
            super(cause);
        }

        public ValidationException(
            String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
