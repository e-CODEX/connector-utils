/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.configuration.ui.vaadin.tools.views;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.HasValidator;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.shared.Registration;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyChecker;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyCollector;
import eu.ecodex.utils.configuration.ui.vaadin.tools.ConfigurationFormsFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.bind.validation.ValidationErrors;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.FieldError;

/**
 * ListConfigurationPropertiesComponent is a UI component for displaying and managing a list of
 * configuration properties in a grid layout. It provides functionalities for validation, setting
 * properties, and marking fields as read-only. This component integrates with Spring Framework for
 * dependency injection, and uses Vaadin framework for UI components.
 */
@Data
@NoArgsConstructor
@SuppressWarnings("squid:S1135")
@org.springframework.stereotype.Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ListConfigurationPropertiesComponent extends VerticalLayout implements
    HasValue<HasValue.ValueChangeEvent<Collection<ConfigurationProperty>>,
        Collection<ConfigurationProperty>>,
    HasValidator<Collection<ConfigurationProperty>> {
    private static final Logger LOGGER =
        LogManager.getLogger(ListConfigurationPropertiesComponent.class);
    Grid<ConfigurationProperty> grid = new Grid<>(ConfigurationProperty.class, false);
    @Autowired
    ConfigurationPropertyCollector configurationPropertyCollector;
    @Autowired
    ConfigurationPropertyChecker configurationPropertyChecker;
    @Autowired
    ConfigurationFormsFactory configurationFormFactory;
    Map<String, String> properties = new HashMap<>();
    Label statusLabel = new Label();
    Binder<Map<String, String>> binder = new Binder();
    private Collection<ConfigurationProperty> configurationProperties = new ArrayList<>();
    private Collection<AbstractField> propertyFields = new ArrayList<>();
    private boolean readOnly = false;

    /**
     * Initializes the component after its construction. This method sets up the data binding,
     * configures the grid that displays properties, and prepares the validation mechanism.
     */
    @PostConstruct
    public void init() {
        binder.setBean(properties);

        grid.addColumn("propertyName").setHeader("Property Path");
        grid.addColumn("label").setHeader("Label");
        grid.addColumn("type").setHeader("Type");
        grid.addComponentColumn(
            (ValueProvider<ConfigurationProperty, Component>) configurationProperty -> {
                var field = configurationFormFactory.createField(configurationProperty, binder);
                propertyFields.add(field);
                return field;
            });
        grid.setDetailsVisibleOnClick(true);
        grid.setItemDetailsRenderer(new ComponentRenderer<>(configProp -> {
            var verticalLayout = new VerticalLayout();
            verticalLayout.add(new Label("Description"));
            verticalLayout.add(new Label(configProp.getDescription()));
            return verticalLayout;
        }));

        grid.setItems(configurationProperties);

        // TODO: add validation error field before ListView

        this.add(this.grid);
        this.add(this.statusLabel);
    }

    /**
     * Sets the configuration properties for the component, updates the grid with these properties,
     * and configures the validation mechanism for the properties.
     *
     * @param configurationProperties The collection of ConfigurationProperty objects to be set and
     *                                displayed in the component.
     */
    public void setConfigurationProperties(
        Collection<ConfigurationProperty> configurationProperties) {
        this.configurationProperties = configurationProperties;
        this.grid.setItems(configurationProperties);

        List<Class> configClasses = configurationProperties
            .stream()
            .map(ConfigurationProperty::getParentClass)
            .distinct()
            .toList();

        binder.withValidator((Validator<Map<String, String>>) (value, context) -> {
            ConfigurationPropertySource configSource =
                new MapConfigurationPropertySource(value);
            List<ValidationErrors> validationErrors =
                configurationPropertyChecker.validateConfiguration(configSource, configClasses);
            if (validationErrors.isEmpty()) {
                return ValidationResult.ok();
            }
            // TODO: improve error representation
            String errString = validationErrors
                .stream().map(err -> err.getAllErrors().stream())
                .flatMap(Function.identity())
                .map(objectError -> {
                    if (objectError instanceof FieldError fieldError) {
                        return fieldError.getObjectName() + "."
                            + fieldError.getField() + ": "
                            + fieldError.getDefaultMessage();
                    }
                    return objectError.getObjectName() + ": "
                        + objectError.getDefaultMessage();
                })
                .collect(Collectors.joining("; "));
            return ValidationResult.error(errString);
        });
    }

    /**
     * Validates the current state of the binder and updates the status label with any validation
     * errors.
     *
     * @return a list of validation results containing any bean validation errors detected during
     *      validation.
     */
    public List<ValidationResult> validate() {
        var validate = this.binder.validate();
        var beanValidationErrors = validate.getBeanValidationErrors();
        LOGGER.trace("BeanValidationErrors: [{}]", beanValidationErrors);
        String collect = beanValidationErrors
            .stream()
            .map(ValidationResult::getErrorMessage)
            .collect(Collectors.joining("\n\n"));
        this.statusLabel.setText(collect);
        return beanValidationErrors;
    }

    /**
     * A validator for a collection of ConfigurationProperty objects. This validator is intended to
     * be used for validating lists of configuration properties within the application.
     */
    public static class ConfigurationPropertiesListValidator
        implements Validator<Collection<ConfigurationProperty>> {
        @Override
        public ValidationResult apply(
            Collection<ConfigurationProperty> value, ValueContext context) {
            return null;
        }
    }

    @Override
    public Validator<Collection<ConfigurationProperty>> getDefaultValidator() {
        return Validator.alwaysPass();
    }

    @Override
    public void setValue(Collection<ConfigurationProperty> value) {
        setConfigurationProperties(value);
    }

    @Override
    public Collection<ConfigurationProperty> getValue() {
        return getConfigurationProperties();
    }

    @Override
    public Registration addValueChangeListener(
        ValueChangeListener<? super ValueChangeEvent<Collection<ConfigurationProperty>>> listener) {
        return null;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        propertyFields.forEach(f -> f.setReadOnly(readOnly));
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        // TODO see why this body is empty
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return false;
    }
}
