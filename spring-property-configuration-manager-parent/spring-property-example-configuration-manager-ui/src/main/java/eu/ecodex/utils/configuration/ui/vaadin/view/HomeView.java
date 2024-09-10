/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.configuration.ui.vaadin.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.Route;

/**
 * Represents the home view of the application, displaying a simple header message.
 *
 * <p>This view is the default route for the application and is displayed within the MainView
 * layout.
 */
@Route(value = "", layout = MainView.class)
public class HomeView extends Div {
    public HomeView() {
        this.add(new H1("I am the Home View"));
    }
}
