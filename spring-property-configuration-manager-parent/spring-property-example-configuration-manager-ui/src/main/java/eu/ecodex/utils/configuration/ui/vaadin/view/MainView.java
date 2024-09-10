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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.NoArgsConstructor;

/**
 * MainView is an AppLayout component that acts as the primary user interface container for the
 * "Spring Properties Configuration Manager" application. It includes a Tab-based navigation system
 * that allows switching between different views.
 */
@HtmlImport("styles/shared-styles.html")

@PageTitle("Spring Properties Configuration Manager")
@NoArgsConstructor
public class MainView extends AppLayout {
    private final Map<Tab, Class<? extends Component>> tabToViewMap = new HashMap<>();
    private final Tabs tabs = new Tabs();

    /**
     * Initializes the MainView component after the bean's properties have been set.
     */
    @PostConstruct
    public void init() {
        this.setContent(new Label("The Content Label"));

        addTab(new Tab("Home"), HomeView.class);
        addTab(new Tab("TreeGridView"), TreeGridView.class);
        addTab(new Tab("ListGridView"), ListGridView.class);
        addTab(new Tab("ConfigFormView"), ConfigFormView.class);

        addToNavbar(tabs);

        tabs.addSelectedChangeListener(this::tabSelected);

        tabs.setSelectedIndex(0);
    }

    private void addTab(Tab tab, Class<? extends Component> viewClazz) {
        this.tabToViewMap.put(tab, viewClazz);
        tabs.add(tab);
    }

    private void tabSelected(Tabs.SelectedChangeEvent selectedChangeEvent) {
        var selectedTab = selectedChangeEvent.getSelectedTab();
        selectedTab.getUI().ifPresent(
            ui -> ui.navigate(tabToViewMap.getOrDefault(selectedTab, HomeView.class)));
    }
}
