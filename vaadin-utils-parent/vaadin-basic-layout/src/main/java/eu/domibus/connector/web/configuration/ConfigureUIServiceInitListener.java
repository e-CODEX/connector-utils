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

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import eu.domibus.connector.web.login.LoginView;
import eu.domibus.connector.web.view.AccessDeniedView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;

/**
 * ConfigureUIServiceInitListener is responsible for initializing the service configuration in the
 * Vaadin application.
 *
 * <p>This listener hooks into the service initialization process to set up listeners that handle
 * user authorization and navigation based on permissions.
 */
@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {
    @Autowired
    AuthenticationManager authenticationManager;

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            final var ui = uiEvent.getUI();
            ui.addBeforeEnterListener(this::beforeEnter);
        });
    }

    /**
     * Reroutes the user if (s)he is not authorized to access the view.
     *
     * @param event before navigation event with event details
     */
    private void beforeEnter(BeforeEnterEvent event) {

        if (!LoginView.class.equals(event.getNavigationTarget())
            && !SecurityUtils.isUserLoggedIn()) {
            event.rerouteTo(LoginView.class);
        }
        Class<?> navigationTarget = event.getNavigationTarget();
        if (!SecurityUtils.isUserAllowedToView(navigationTarget)) {
            event.rerouteTo(AccessDeniedView.class);
        }
    }
}
