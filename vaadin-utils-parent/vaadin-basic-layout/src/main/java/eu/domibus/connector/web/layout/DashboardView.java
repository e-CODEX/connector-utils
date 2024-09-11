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

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

/**
 * DashboardView is the main view of the Domibus Connector Administration UI. It serves as the
 * landing page once a user successfully logs in. The view displays a welcoming message to the
 * user.
 */
@UIScope
@Route(value = DashboardView.ROUTE, layout = BasicLayout.class)
@PageTitle("domibusConnector - Administrator")
public class DashboardView extends VerticalLayout {
    public static final String ROUTE = "";
    Label welcomeLabel = new Label();

    public DashboardView() {
        welcomeLabel.setText("Welcome to Domibus Connector Administration UI");
        add(welcomeLabel);
    }
}
