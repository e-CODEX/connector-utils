/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.service.impl;

import eu.domibus.connector.service.IUserPasswordService;
import eu.domibus.connector.web.auth.exception.NotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Default implementation of the IUserPasswordService interface. This service provides methods for
 * authenticating users with a username and password. It also includes functionality for changing a
 * user's password, though this feature may not be supported depending on the specific
 * authentication implementation.
 */
@Component
@ConditionalOnMissingBean
public class DefaultUserPasswordService implements IUserPasswordService {
    @Autowired
    AuthenticationManager authenticationManager;

    public void changePasswordLogin(String username, String oldPassword, String newPassword)
        throws NotSupportedException {
        throw new NotSupportedException(
            "", "The current authentication implementation does not support password change!");
    }

    @Override
    public void passwordLogin(String username, String password) throws AuthenticationException {
        var usernamePasswordAuthenticationToken =
            new UsernamePasswordAuthenticationToken(username, password);
        var authentication =
            authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        if (authentication.isAuthenticated()) {
            var context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
        }
    }
}
