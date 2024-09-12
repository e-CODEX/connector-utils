/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.configuration.ui.vaadin.tools.configforms;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.shared.Registration;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyCollector;
import eu.ecodex.utils.configuration.ui.vaadin.tools.ConfigurationFieldFactory;
import eu.ecodex.utils.configuration.ui.vaadin.tools.ConfigurationFormsFactory;
import eu.ecodex.utils.configuration.ui.vaadin.tools.UiConfigurationConversationService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.convert.ConversionService;

/**
 * Implementation of the ConfigurationFormsFactory interface to create forms and form fields based
 * on configuration properties annotated with {@link ConfigurationProperties}.
 */
@SuppressWarnings("squid:S1135")
@org.springframework.stereotype.Component
public class ConfigurationFormsFactoryImpl implements ConfigurationFormsFactory {
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationFormsFactoryImpl.class);
    @Autowired
    ConfigurationPropertyCollector configurationPropertyCollector;
    @Autowired(required = false)
    List<ConfigurationFieldFactory> fieldCreatorFactories = new ArrayList<>();
    @Autowired
    jakarta.validation.Validator validator;
    @Autowired
    @UiConfigurationConversationService
    ConversionService conversionService;

    /**
     * Creates a ConfigurationPropertyForm instance based on the given class that is annotated
     * with @ConfigurationProperties. The method validates the class, collects its configuration
     * properties, and binds them to a Vaadin form layout.
     *
     * @param clazz The class annotated with @ConfigurationProperties from which to create the form.
     * @return A ConfigurationPropertyForm instance populated with fields representing the
     *      configuration properties of the class.
     * @throws IllegalArgumentException If the provided class is not annotated with
     *      {@code @ConfigurationProperties.}
     */
    public ConfigurationPropertyForm createFormFromConfigurationPropertiesClass(Class clazz) {
        if (!clazz.isAnnotationPresent(ConfigurationProperties.class)) {
            throw new IllegalArgumentException(
                "the passed class must be annotated with " + ConfigurationProperties.class);
        }
        Collection<ConfigurationProperty> configurationPropertyFromClazz =
            configurationPropertyCollector.getConfigurationPropertyFromClazz(clazz);

        Binder<Map<String, String>> binder = new Binder<>();

        var formLayout = new ConfigurationPropertyForm(clazz, binder);

        configurationPropertyFromClazz.forEach(prop -> {
            var component = createComponentFromConfigurationProperty(prop, binder);
            formLayout.add(component);
        });

        formLayout.setValue(new HashMap<>()); // setting empty properties...

        return formLayout;
    }

    /**
     * Creates a Vaadin Component from the given ConfigurationProperty and data Binder.
     *
     * @param prop The configuration property to create the component for.
     * @param binder The data binder that binds the property values.
     * @return A component that represents the configuration property.
     */
    public Component createComponentFromConfigurationProperty(
        ConfigurationProperty prop, Binder<Map<String, String>> binder) {
        var field = createField(prop, binder);

        field.setId(prop.getPropertyName());

        var label = new NativeLabel();

        label.setFor(field);
        label.setText(prop.getBeanPropertyName());

        var horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(label);
        horizontalLayout.add(field);

        var infoButton = new Button();
        infoButton.setIcon(new Icon(VaadinIcon.INFO));
        infoButton.addClickListener(clickEvent -> {
            // TODO: show info box about property...
        });
        horizontalLayout.add(infoButton);

        return horizontalLayout;
    }

    /**
     * Creates an {@link AbstractField} for a given configuration property.
     *
     * @param prop The configuration property to create the field for.
     * @param binder The data binder that binds the property values.
     * @return An {@link AbstractField} instance that corresponds to the given configuration
     *      property.
     * @throws RuntimeException If no suitable field factory is found for the property's type.
     */
    public AbstractField createField(
        final ConfigurationProperty prop, final Binder<Map<String, String>> binder) {
        Optional<ConfigurationFieldFactory> ff = fieldCreatorFactories
            .stream()
            .filter(
                configurationFieldFactory -> configurationFieldFactory.canHandle(prop.getType()))
            .findFirst();
        AbstractField field;
        if (ff.isPresent()) {
            var configurationFieldFactory = ff.get();
            field = configurationFieldFactory.createField(prop, binder);
            return field;
        } else {
            throw new RuntimeException(
                String.format("No Field Factory found for property %s with type [%s]", prop,
                              prop.getType()
                ));
            // Just create a simple text field...
        }
    }

    /**
     * Represents a Vaadin form layout for configuration properties.
     * This form is designed to handle mappings of property keys and values.
     * It extends the FormLayout class and implements the HasValue interface to manage
     * value changes and validation for the configuration properties.
     */
    public class ConfigurationPropertyForm extends FormLayout
        implements HasValue<HasValue.ValueChangeEvent<Map<String, String>>, Map<String, String>> {
        private NativeLabel formStatusLabel = new NativeLabel();
        /**
         * The by the factory generated binder.
         */
        private final Binder<Map<String, String>> binder;
        /**
         * The specific type of the with @ConfigurationProperties annotated class.
         */
        private final Class clazz;
        private boolean readOnly;
        private Map<String, String> properties;

        private ConfigurationPropertyForm(Class clazz, Binder binder) {
            this.binder = binder;
            this.clazz = clazz;
            var defaultHandler = binder.getValidationStatusHandler();

            // see: https://vaadin.com/docs/v10/flow/binding-data/tutorial-flow-components-binder-beans.html
            binder.setValidationStatusHandler(status -> {
                LOGGER.info("Binder validation status handler called: [{}]", status);
                // create an error message on failed bean level validations
                List<ValidationResult> errors = status.getBeanValidationErrors();

                // collect all bean level error messages into a single string,
                // separating each message with a <br> tag
                String errorMessage = errors.stream()
                                            .map(ValidationResult::getErrorMessage)
                                            // sanitize the individual error strings to avoid code
                                            // injection
                                            // since we are displaying the resulting string as HTML
                                            .map(errorString -> Jsoup.clean(
                                                errorString,
                                                Safelist.simpleText()
                                            ))
                                            .collect(Collectors.joining("<br>"));

                // finally, display all bean level validation errors in a single
                // label
                formStatusLabel.getElement().setProperty("innerHTML", errorMessage);

                // Let the default handler show messages for each field
                defaultHandler.statusChange(status);
            });
            this.addComponentAsFirst(formStatusLabel);
            formStatusLabel.setVisible(true);
        }

        @Override
        public void setValue(Map<String, String> value) {
            this.properties = value;
            this.binder.setBean(value);
        }

        @Override
        public Map<String, String> getValue() {
            return this.binder.getBean();
        }

        @Override
        public Registration addValueChangeListener(ValueChangeListener valueChangeListener) {
            return null;
        }

        @Override
        public void setReadOnly(boolean b) {
            this.readOnly = b;
        }

        @Override
        public boolean isReadOnly() {
            return readOnly;
        }

        @Override
        public void setRequiredIndicatorVisible(boolean b) {
            // TODO see why this body is empty
        }

        @Override
        public boolean isRequiredIndicatorVisible() {
            return false;
        }

        public Binder getBinder() {
            return binder;
        }
    }
}
