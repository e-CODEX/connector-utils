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

import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreAndTrustStoreConfigurationProperties;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

/**
 * A constraint validator that checks if a key can be loaded from a given key store configuration.
 */
public class KeyFromKeyAndTrustStoreLoadable implements
    ConstraintValidator<CheckKeyIsLoadableFromKeyStore,
        KeyAndKeyStoreAndTrustStoreConfigurationProperties> {
    private Validator validator;

    @Override
    public void initialize(CheckKeyIsLoadableFromKeyStore constraintAnnotation) {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Override
    public boolean isValid(
        KeyAndKeyStoreAndTrustStoreConfigurationProperties value,
        ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Set<ConstraintViolation<KeyAndKeyStoreAndTrustStoreConfigurationProperties>>
            constraintViolations;
        constraintViolations = validator.validateProperty(value, "privateKey");
        constraintViolations.addAll(validator.validateProperty(value, "keyStore"));

        if (!constraintViolations.isEmpty()) {
            return false;
        }

        return HelperMethods.checkKeyIsLoadable(
            context, value.getKeyStore(), value.getPrivateKey());
    }
}
