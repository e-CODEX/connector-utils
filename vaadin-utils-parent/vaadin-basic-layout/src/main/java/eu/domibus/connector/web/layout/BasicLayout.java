/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.web.layout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.web.utils.TabViewRouterHelper;
import lombok.NoArgsConstructor;

/**
 * BasicLayout is a custom layout class extending the AppLayout and implementing the
 * BeforeEnterObserver interface. It leverages the TabViewRouterHelper to manage tab navigation and
 * visibility based on user roles.
 */
@UIScope
@NoArgsConstructor
@org.springframework.stereotype.Component
public class BasicLayout extends AppLayout implements BeforeEnterObserver {
    protected TabViewRouterHelper tabViewRouterHelper = new TabViewRouterHelper();

    public void beforeEnter(BeforeEnterEvent event) {
        tabViewRouterHelper.beforeEnter(event);
    }
}
