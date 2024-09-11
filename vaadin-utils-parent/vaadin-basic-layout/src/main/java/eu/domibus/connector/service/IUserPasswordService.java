/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.service;

import org.springframework.security.core.AuthenticationException;

/**
 * Interface for the username password login service maybe rename this interface to a more generic
 * name also add support to redirect to central login service.
 */
@SuppressWarnings("squid:S1135")
public interface IUserPasswordService {
    void changePasswordLogin(String username, String value, String value1)
        throws AuthenticationException;

    void passwordLogin(String username, String password) throws AuthenticationException;

    // TODO: add method with supported features, listUsers, changePassword, maybe with an reroute
    //  option
}
