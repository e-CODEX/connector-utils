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

import eu.ecodex.utils.monitor.gw.config.GatewayMonitorConfigurationProperties;
import eu.ecodex.utils.monitor.gw.domain.AccessPoint;
import eu.ecodex.utils.monitor.gw.dto.AccessPointStatusDTO;
import eu.ecodex.utils.monitor.gw.dto.CheckResultDTO;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;

/**
 * Service for checking the status of gateways.
 *
 * <p>This service provides methods to get the current status of a specified gateway and caches the
 * results based on a configurable timeout duration.
 *
 * <p>The service utilizes SSL/TLS configurations to securely connect and retrieve the statuses
 * from the gateways.
 */
@Component
@SuppressWarnings("squid:S1135")
public class GatewaysCheckerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GatewaysCheckerService.class);
    @Autowired
    GatewayMonitorConfigurationProperties gatewayMonitorConfig;
    @Autowired
    TrustStoreCompleteChainTrustStrategy trustStoreCompleteChainTrustStrategy;
    private final Map<AccessPoint, AccessPointStatusDTO> apCheck = new HashMap<>();

    public AccessPointStatusDTO getGatewayStatus(AccessPoint ap) {
        return getGatewayStatus(ap, gatewayMonitorConfig.getCheckCacheTimeout());
    }

    /**
     * Retrieves the current status of the specified gateway.
     *
     * @param ap           The access point representing the gateway whose status needs to be
     *                     fetched.
     * @param cacheTimeout The time duration for which the status should be cached before checking
     *                     again.
     * @return An AccessPointStatusDTO object containing the status of the gateway, including TLS
     *      configurations, certificates, failures, warnings, and other metadata regarding the
     *      connection.
     */
    public AccessPointStatusDTO getGatewayStatus(AccessPoint ap, Duration cacheTimeout) {
        var status = apCheck.get(ap);
        if (status != null && status.getCheckTime().plus(cacheTimeout)
                                    .isAfter(ZonedDateTime.now())) {
            LOGGER.trace(
                "Checking [{}] and hitting [{}] + [{}] cache last check was on [{}]", ap,
                ZonedDateTime.now(), cacheTimeout, status.getCheckTime()
            );
            return status;
        }
        LOGGER.info("Checking endpoint [{}]", ap);
        status = new AccessPointStatusDTO();
        status.setCheckTime(ZonedDateTime.now());
        status.setEndpoint(ap.getEndpoint());
        status.setName(ap.getName());
        apCheck.put(ap, status);

        char[] privateKeyPassword =
            gatewayMonitorConfig.getTls().getPrivateKey().getPassword().toCharArray();
        var keyStore = gatewayMonitorConfig.getTls().getKeyStore().loadKeyStore();
        var trustStore = gatewayMonitorConfig.getTls().getTrustStore().loadKeyStore();

        var minTlsString = gatewayMonitorConfig.getTls().getMinTls();

        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContexts
                .custom()
                .loadTrustMaterial(trustStore, trustStoreCompleteChainTrustStrategy)
                .loadKeyMaterial(
                    keyStore, privateKeyPassword,
                    (aliases, sslParameters) -> gatewayMonitorConfig.getTls()
                                                                    .getPrivateKey()
                                                                    .getAlias()
                )
                .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException
                 | UnrecoverableKeyException e) {
            LOGGER.error("Error while setting up SSLContext", e);
            var checkResultDTO = new CheckResultDTO();
            checkResultDTO.setName("SSLContext setup");
            checkResultDTO.setMessage(e.getMessage());
            checkResultDTO.writeStackTraceIntoDetails(e);
        }

        LOGGER.trace(
            "Client supports: [{}]", CollectionUtils.arrayToList(
                sslcontext.getSupportedSSLParameters().getProtocols()));

        final ProtocolVersion[] allowedTls;
        ProtocolVersion[] supportedClientProtos =
            Stream.of(sslcontext.getSupportedSSLParameters().getProtocols())
                  .map(s -> {
                      try {
                          return TLS.parse(s);
                      } catch (ParseException e) {
                          return null;
                      }
                  })
                  .filter(Objects::nonNull)
                  .toArray(ProtocolVersion[]::new);
        LOGGER.debug(
            "Supported and Allowed client protocols are [{}]",
            CollectionUtils.arrayToList(supportedClientProtos)
        );

        ProtocolVersion minTls;
        try {
            minTls = TLS.parse(minTlsString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        allowedTls = Stream.of(TLS.values())
                           .filter(tls -> tls.greaterEquals(minTls))
                           .map(t -> t.version)
                           .filter(p -> ArrayUtils.contains(supportedClientProtos, p))
                           .toArray(ProtocolVersion[]::new);
        status.setAllowedTls(allowedTls);

        LOGGER.trace(
            "allowed TLS protocols are [{}]", CollectionUtils.arrayToList(status.getAllowedTls()));

        if (allowedTls.length == 0) {
            var checkResultDTO = new CheckResultDTO();
            checkResultDTO.setMessage("Client does not support minTls!");
            status.getFailures().add(checkResultDTO);
            LOGGER.warn(
                "Client supports TLS protocols [{}] but required minTls [{}] is not part of it!",
                CollectionUtils.arrayToList(supportedClientProtos), minTls
            );
        }

        var defaultHostnameVerifier = new DefaultHostnameVerifier();

        TLS[] tls = Stream.of(allowedTls)
                          .map(this::mapProtocolVersionToTLS)
                          .toArray(TLS[]::new);

        final SSLConnectionSocketFactory sslSocketFactory =
            SSLConnectionSocketFactoryBuilder.create()
                                             .setSslContext(sslcontext)
                                             .setTlsVersions(tls)
                                             .setHostnameVerifier(defaultHostnameVerifier)
                                             .build();

        final var cm = PoolingHttpClientConnectionManagerBuilder.create()
                                                                .setSSLSocketFactory(
                                                                    sslSocketFactory)
                                                                .build();
        try (var httpclient = HttpClients.custom().setConnectionManager(cm).build()) {
            final var httpRequest = new HttpGet(ap.getEndpoint());

            LOGGER.debug(
                "Executing request {} {}", httpRequest.getMethod(), httpRequest.getUri()
            );

            final var clientContext = HttpClientContext.create();
            try (CloseableHttpResponse response = httpclient.execute(httpRequest, clientContext)) {
                LOGGER.debug("----------------------------------------");
                LOGGER.debug("{} {}", response.getCode(), response.getReasonPhrase());
                LOGGER.debug(EntityUtils.toString(response.getEntity()));

                if (response.getCode() != 200) {
                    var checkResultDTO = new CheckResultDTO();
                    checkResultDTO.setName("HTTP Code");
                    checkResultDTO.setMessage("HTTP Code != 200");
                    status.getFailures().add(checkResultDTO);
                }
            } catch (SSLHandshakeException sslHandshakeException) {
                LOGGER.error("TLS Handshake failed due", sslHandshakeException);

                var checkResultDTO = new CheckResultDTO();
                checkResultDTO.setName("TLS failure");
                checkResultDTO.setMessage("TLS Handshake failed!");

                // TODO: switch for print stack trace...
                checkResultDTO.writeStackTraceIntoDetails(sslHandshakeException);
                status.getFailures().add(checkResultDTO);
            } finally {
                final var sslSession = clientContext.getSSLSession();
                if (sslSession != null) {
                    LOGGER.debug("TLS protocol {}", sslSession.getProtocol());
                    LOGGER.debug("TLS cipher suite {}", sslSession.getCipherSuite());

                    status.setUsedTls(TLS.parse(sslSession.getProtocol()));
                    status.setLocalCertificates(
                        convertToBase64StringArray(sslSession.getLocalCertificates()));
                    status.setServerCertificates(
                        convertToBase64StringArray(sslSession.getPeerCertificates()));
                } else {
                    LOGGER.info("SSL session is null, cannot provide any information!");
                }
                status.setProxyHost(clientContext.getHttpRoute().getProxyHost());
                status.setTargetHost(clientContext.getHttpRoute().getTargetHost());
            }
        } catch (IOException | ParseException | URISyntaxException | IllegalArgumentException e) {
            var checkResultDTO = new CheckResultDTO();
            checkResultDTO.setName("Connection Failure");
            checkResultDTO.setMessage("Connection failed");
            checkResultDTO.writeStackTraceIntoDetails(e);
            status.getFailures().add(checkResultDTO);
        }
        return status;
    }

    private TLS mapProtocolVersionToTLS(ProtocolVersion protocolVersion) {
        return Stream.of(TLS.values())
                     .filter(t -> t.isSame(protocolVersion))
                     .findFirst()
                     .get();
    }

    private String[] convertToBase64StringArray(java.security.cert.Certificate[] certificates) {
        if (certificates == null) {
            return new String[0];
        }
        return Stream.of(certificates)
                     .map(this::mapToBase64String)
                     .toArray(String[]::new);
    }

    private String mapToBase64String(Certificate certificate) {
        try {
            byte[] encoded = certificate.getEncoded();
            return Base64Utils.encodeToString(encoded);
        } catch (CertificateEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
