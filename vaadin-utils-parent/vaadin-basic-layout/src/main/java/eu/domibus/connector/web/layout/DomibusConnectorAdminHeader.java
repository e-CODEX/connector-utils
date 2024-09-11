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

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.web.configuration.SecurityUtils;
import eu.domibus.connector.web.utils.ViewConstant;

/**
 * DomibusConnectorAdminHeader is a UI component that represents the header section of the Domibus
 * Connector Administration interface. It includes logos and information about the current user.
 */
@SpringComponent
@UIScope
public class DomibusConnectorAdminHeader extends HorizontalLayout implements BeforeEnterObserver {
    Label currentUser = new Label();

    /**
     * Constructor.
     */
    public DomibusConnectorAdminHeader() {
        var ecodexLogo = new Div();
        var ecodex = new Image("frontend/images/logo_ecodex_0.png", "eCodex");
        ecodex.setHeight("70px");
        ecodexLogo.add(ecodex);
        ecodexLogo.setHeight("70px");

        var adminLabel = new Label("domibusConnector - Administration");
        adminLabel.getStyle().set(ViewConstant.FONT_SIZE_STYLE, "30px");
        adminLabel.getStyle().set(ViewConstant.FONT_STYLE, "italic");
        adminLabel.getStyle().set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_GREY);

        var domibusConnector = new Div();
        domibusConnector.add(adminLabel);
        domibusConnector.getStyle()
                        .set(ViewConstant.ALIGNMENT_STYLE, ViewConstant.ALIGNMENT_CENTER);

        var europaLogo = new Div();
        var europa = new Image("frontend/images/europa-logo.jpg", "europe");
        europa.setHeight("50px");
        europaLogo.add(europa);
        europaLogo.setHeight("50px");

        add(ecodexLogo, domibusConnector, europaLogo, currentUser);
        setAlignItems(Alignment.CENTER);
        expand(domibusConnector);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setWidth("95%");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (SecurityUtils.isUserLoggedIn()) {
            currentUser.setText("User: " + SecurityUtils.getUsername());
        } else {
            currentUser.setText("");
        }
    }
}
