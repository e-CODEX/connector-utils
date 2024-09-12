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

import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreConfigurationProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.HashSet;
import java.util.Set;

/**
 * Validator to check if a specific key is loadable from a configured key store.
 */
public class KeyFromKeyStoreLoadableValidator implements
    ConstraintValidator<CheckKeyIsLoadableFromKeyStore, KeyAndKeyStoreConfigurationProperties> {
    private Validator validator;

    @Override
    public void initialize(CheckKeyIsLoadableFromKeyStore constraintAnnotation) {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Override
    public boolean isValid(
        KeyAndKeyStoreConfigurationProperties value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Set<ConstraintViolation<KeyAndKeyStoreConfigurationProperties>> validations =
            new HashSet<>();
        Set<ConstraintViolation<KeyAndKeyStoreConfigurationProperties>> path =
            validator.validateProperty(value, "privateKey");
        validations.addAll(path);
        Set<ConstraintViolation<KeyAndKeyStoreConfigurationProperties>> keyStoreValidation =
            validator.validateProperty(value, "keyStore");
        validations.addAll(keyStoreValidation);
        if (!validations.isEmpty()) {
            return false;
        }

        context.disableDefaultConstraintViolation();

        return HelperMethods.checkKeyIsLoadable(
            context, value.getKeyStore(), value.getPrivateKey());
    }
}
