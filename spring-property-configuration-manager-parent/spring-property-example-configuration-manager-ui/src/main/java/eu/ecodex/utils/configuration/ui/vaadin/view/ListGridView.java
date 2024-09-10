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

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.example1.Example1ConfigurationProperties;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyCollector;
import eu.ecodex.utils.configuration.ui.vaadin.tools.views.ListConfigurationPropertiesComponent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * ListGridView represents the user interface component responsible for displaying and managing a
 * grid of configuration properties in the "Spring Properties Configuration Manager" application.
 */
@NoArgsConstructor
@SuppressWarnings("squid:S1135")
@HtmlImport("styles/shared-styles.html")
@Route(value = "listgridview", layout = MainView.class)
@PageTitle("Spring Properties Configuration Manager")
public class ListGridView extends VerticalLayout {
    private static final Logger LOGGER = LogManager.getLogger(ListGridView.class);
    @Autowired
    ListConfigurationPropertiesComponent listConfigurationPropertiesComponent;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ConfigurationPropertyCollector configurationPropertyCollector;
    private Map<String, String> properties = new HashMap<>();
    Button saveProperties;
    Button resetProperties;
    Button setConfigClasses;

    @PostConstruct
    public void init() {
        initUi();
        initProperties();
    }

    private void initUi() {
        setConfigClasses = new Button("set Config Classes");
        setConfigClasses.addClickListener(this::setConfigClasses);
        this.add(setConfigClasses);
        saveProperties = new Button("Save Properties");
        saveProperties.addClickListener(this::saveProperties);
        this.add(saveProperties);
        resetProperties = new Button("Reset Properties");
        resetProperties.addClickListener(this::resetProperties);
        this.add(resetProperties);
        this.add(listConfigurationPropertiesComponent);
    }

    private void setConfigClasses(ClickEvent<Button> buttonClickEvent) {
        List<ConfigurationProperty> configurationProperties =
            Stream.of(Example1ConfigurationProperties.class)
                  .map(clz -> configurationPropertyCollector
                      .getConfigurationPropertyFromClazz(clz)
                      .stream())
                  .flatMap(Function.identity()).toList();

        listConfigurationPropertiesComponent.setConfigurationProperties(configurationProperties);
    }

    private void resetProperties(ClickEvent<Button> buttonClickEvent) {
        initProperties();
    }

    private void saveProperties(ClickEvent<Button> buttonClickEvent) {
        // TODO: call validator
        listConfigurationPropertiesComponent.validate();
    }

    private void initProperties() {
        List<ConfigurationProperty> configurationProperties =
            Stream.of(Example1ConfigurationProperties.class)
                  .map(clz -> configurationPropertyCollector.getConfigurationPropertyFromClazz(clz)
                                                            .stream())
                  .flatMap(Function.identity()).toList();

        configurationProperties
            .stream()
            .map(ConfigurationProperty::getPropertyName)
            .filter(propName -> applicationContext.getEnvironment().getProperty(propName) != null)
            .forEach(propName -> properties.put(propName, applicationContext.getEnvironment()
                                                                            .getProperty(propName)
            ));

        LOGGER.trace("Setting properties on gui table [{}]", properties);

        listConfigurationPropertiesComponent.getBinder().setBean(properties);
    }
}
