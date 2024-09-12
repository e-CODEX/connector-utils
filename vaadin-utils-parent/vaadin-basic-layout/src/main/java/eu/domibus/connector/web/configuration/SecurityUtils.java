/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.web.configuration;

import com.vaadin.flow.server.HandlerHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import eu.domibus.connector.web.utils.RoleRequired;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * SecurityUtils takes care of all such static operations that have to do with security and querying
 * rights from different beans of the UI.
 */
public class SecurityUtils {
    private static final Logger LOGGER = LogManager.getLogger(SecurityUtils.class);

    private SecurityUtils() {
        // Util methods only
    }

    /**
     * Tests if the request is an internal framework request. The test consists of checking if the
     * request parameter is present and if its value is consistent with any of the request types
     * know.
     *
     * @param request {@link HttpServletRequest}
     * @return true if is an internal framework request. False otherwise.
     */
    static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        final String parameterValue =
            request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
            && Stream.of(HandlerHelper.RequestType.values())
                     .anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }

    /**
     * Tests if some user is authenticated. As Spring Security always will create an
     * {@link AnonymousAuthenticationToken} we have to ignore those tokens explicitly.
     */
    public static boolean isUserLoggedIn() {
        Authentication authentication = getAuthentication();
        return authentication != null
            && !(authentication instanceof AnonymousAuthenticationToken)
            && authentication.isAuthenticated();
    }

    /**
     * Determines whether the current user is allowed to view the specified class.
     * This is done by checking if the class is annotated with {@link RoleRequired}
     * and whether the user has the required role.
     *
     * @param viewClass The class to check for view permissions.
     * @return {@code true} if the user is allowed to view the class, {@code false} otherwise.
     */
    public static boolean isUserAllowedToView(Class<?> viewClass) {
        RoleRequired annotation = AnnotationUtils.findAnnotation(viewClass, RoleRequired.class);
        if (annotation != null) {
            String role = annotation.role();
            LOGGER.debug(
                "#isUserAllowedToView: checking if user is in requiredRole [{}] of view [{}]", role,
                viewClass
            );
            return isUserInRole(role);
        }
        LOGGER.debug(
            "#isUserAllowedToView: View [{}] has no required role, returning true", viewClass);
        return true;
    }

    /**
     * Retrieves the username of the currently authenticated user.
     *
     * @return the username of the currently authenticated user as a {@code String}, or an empty
     *      {@code String} if no user is authenticated.
     */
    public static String getUsername() {
        var authentication = getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal == null) {
            return "";
        }
        return principal.toString();
    }

    /**
     * Checks if the currently authenticated user has the specified role.
     *
     * @param role the name of the role to check for, without the "ROLE_" prefix.
     * @return true if the user is authenticated and has the specified role, false otherwise.
     */
    public static boolean isUserInRole(String role) {
        boolean userHasRole = false;
        if (isUserLoggedIn()) {
            var authentication = getAuthentication();

            userHasRole = authentication.getAuthorities()
                                        .stream()
                                        .anyMatch(grantedAuthority -> ("ROLE_" + role).equals(
                                            ((GrantedAuthority) grantedAuthority).getAuthority()));

            LOGGER.trace(
                "User [{}] has roles [{}]", authentication.getPrincipal(),
                authentication.getAuthorities()
            );
        }

        LOGGER.debug(
            "Check if user is logged in and has role [{}] returned [{}]", role, userHasRole);
        return userHasRole;
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
