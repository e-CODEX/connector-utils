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
import eu.ecodex.utils.configuration.example1.Example1ConfigurationProperties;
import eu.ecodex.utils.configuration.ui.vaadin.tools.ConfigurationFormsFactory;
import eu.ecodex.utils.configuration.ui.vaadin.tools.configforms.ConfigurationFormsFactoryImpl;
import javax.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ConfigFormView is a Vaadin VerticalLayout that represents a configuration form for managing
 * Spring properties.
 *
 * <p>This view is accessible via the route name specified by the ROUTE_NAME constant, and it uses
 * MainView as its layout. The view is titled "Spring Properties Configuration Manager".
 */
@HtmlImport("styles/shared-styles.html")
@Route(value = ConfigFormView.ROUTE_NAME, layout = MainView.class)
@PageTitle("Spring Properties Configuration Manager")
@NoArgsConstructor
public class ConfigFormView extends VerticalLayout {
    public static final String ROUTE_NAME = "configform";
    private static final Logger LOGGER = LogManager.getLogger(ConfigFormView.class);
    @Autowired
    ConfigurationFormsFactory configurationFormFactory;
    Button button = new Button("Check");
    ConfigurationFormsFactoryImpl.ConfigurationPropertyForm formFromConfigurationPropertiesClass;

    /**
     * Initializes the configuration form view by creating a form from the
     * {@link Example1ConfigurationProperties} class and adding it along with a button to the
     * layout. Also sets up a click listener for the button.
     */
    @PostConstruct
    public void init() {
        formFromConfigurationPropertiesClass =
            configurationFormFactory.createFormFromConfigurationPropertiesClass(
                Example1ConfigurationProperties.class);
        this.add(formFromConfigurationPropertiesClass);
        this.add(button);

        button.addClickListener(this::checkButtonClicked);
    }

    private void checkButtonClicked(ClickEvent<Button> buttonClickEvent) {
        LOGGER.info("check button clicked");
        var validate = formFromConfigurationPropertiesClass.getBinder().validate();
        LOGGER.info("validate result: [{}]", validate.getBeanValidationResults());
    }
}
