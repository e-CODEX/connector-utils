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

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.web.login.LoginView;
import eu.domibus.connector.web.utils.ViewConstant;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This class represents a user information layout, which includes the username display and a logout
 * button. It extends HorizontalLayout and implements AfterNavigationObserver to update the username
 * after navigation events.
 */
@UIScope
@org.springframework.stereotype.Component
public class UserInfo extends HorizontalLayout implements AfterNavigationObserver {
    NativeLabel username;

    /**
     * Constructor.
     */
    public UserInfo() {
        var userDiv = new HorizontalLayout();
        var userIcon = new Icon(VaadinIcon.USER);
        userIcon.getStyle().set("margin-right", "10px");
        userIcon.setSize("20px");
        userDiv.add(userIcon);
        username = new NativeLabel("");
        username.getStyle().set(ViewConstant.FONT_SIZE_STYLE, "15px");
        userDiv.add(username);
        add(userDiv);

        var logoutDiv = new Div();
        logoutDiv.getStyle().set(ViewConstant.ALIGNMENT_STYLE, ViewConstant.ALIGNMENT_CENTER);
        logoutDiv.getStyle().set("padding", "10px");
        var logoutButton = new Button("Logout");
        logoutButton.addClickListener(e -> {
            var logout2Div = new Div();
            var logoutText = new NativeLabel("Logout call success!");
            logoutText.getStyle().set("font-weight", "bold");
            logoutText.getStyle().set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_RED);
            logout2Div.add(logoutText);
            logout2Div.getStyle().set(ViewConstant.ALIGNMENT_STYLE, ViewConstant.ALIGNMENT_CENTER);
            logout2Div.setVisible(true);

            var logoutDialog = new Dialog();
            logoutDialog.add(logout2Div);

            var okContent = new Div();
            okContent.getStyle().set(ViewConstant.ALIGNMENT_STYLE, ViewConstant.ALIGNMENT_CENTER);
            okContent.getStyle().set("padding", "10px");
            var okButton = new Button("OK");
            okButton.addClickListener(e2 -> {
                SecurityContextHolder.getContext().setAuthentication(null);
                logoutDialog.close();
                this.getUI().ifPresent(ui -> ui.navigate(LoginView.class));
            });
            okContent.add(okButton);

            logoutDialog.add(okContent);

            logoutDialog.open();
        });
        logoutDiv.add(logoutButton);
        add(logoutDiv);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.END);
        setWidth("95%");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        var context = SecurityContextHolder.getContext();
        if (context != null) {
            Object principal = context.getAuthentication().getPrincipal();
            this.username.setText(principal.toString());
        }
    }
}
