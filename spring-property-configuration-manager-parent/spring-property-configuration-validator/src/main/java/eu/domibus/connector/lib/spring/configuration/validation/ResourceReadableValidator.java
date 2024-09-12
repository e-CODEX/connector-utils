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

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.io.IOException;
import org.springframework.core.io.Resource;

/**
 * ResourceReadableValidator is a class that implements the ConstraintValidator interface to
 * validate whether a given Resource is readable. This validator is specifically used in conjunction
 * with the @CheckResourceIsReadable annotation.
 */
@SuppressWarnings("squid:S1135")
public class ResourceReadableValidator
    implements ConstraintValidator<CheckResourceIsReadable, Resource> {
    @Override
    public void initialize(CheckResourceIsReadable constraintAnnotation) {
        // TODO check why the body is empty
    }

    @Override
    public boolean isValid(Resource value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        try {
            var inputStream = value.getInputStream();
            if (inputStream == null) {
                var message = String.format(
                    "Cannot open provided resource [%s]! Check if the path is correct and exists!",
                    value
                );
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                return false;
            }
            inputStream.close();
        } catch (IOException e) {
            var message = String.format(
                "Cannot open provided resource [%s]! Check if the path is correct and exists!",
                value
            );
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        return true;
    }
}
