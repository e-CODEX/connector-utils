/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.web.auth.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.AuthenticationException;

/**
 * This exception will be thrown if the requested user action is not supported eg. password change
 * not supported.
 */
@Getter
@Setter
public class NotSupportedException extends AuthenticationException {
    /**
     * A url to a service which is able to do the requested action. eg Authentication Portal.
     */
    String redirectUrl;

    public NotSupportedException(String redirectUrl, String msg, Throwable t) {
        super(msg, t);
        this.redirectUrl = redirectUrl;
    }

    public NotSupportedException(String redirectUrl, String msg) {
        super(msg);
        this.redirectUrl = redirectUrl;
    }
}
