/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.lib.spring.configuration.validation;

import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validator implementation to ensure that a store specified by {@link StoreConfigurationProperties}
 * is loadable and valid according to the constraints defined by the {@link CheckStoreIsLoadable}
 * annotation.
 *
 * <p>This class checks that the 'path' property of {@link StoreConfigurationProperties} can be
 * validated and that the key store can be loaded without errors.
 *
 * <p>Errors during validation or loading process are logged, and appropriate constraint violations
 * are raised.
 */
@SuppressWarnings("squid:S1135")
public class StoreLoadableValidator
    implements ConstraintValidator<CheckStoreIsLoadable, StoreConfigurationProperties> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreLoadableValidator.class);
    private Validator validator;

    @Override
    public void initialize(CheckStoreIsLoadable constraintAnnotation) {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Override
    public boolean isValid(StoreConfigurationProperties value, ConstraintValidatorContext context) {
        try {
            if (value == null) {
                return true;
            }
            Set<ConstraintViolation<StoreConfigurationProperties>> path =
                validator.validateProperty(value, "path");
            if (!path.isEmpty()) {
                return false;
            }
            try {
                value.loadKeyStore();
            } catch (StoreConfigurationProperties.CannotLoadKeyStoreException exception) {
                // TODO: nice message!
                context.buildConstraintViolationWithTemplate(exception.getCause().getMessage())
                       .addConstraintViolation();
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("exception occured", e);
            return false;
        }

        return true;
    }
}
