/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.configuration.example1.validators;

import static eu.ecodex.utils.configuration.example1.validators.StringComparisonMode.EQUAL;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Annotation to compare multiple string properties of a class based on specified criteria.
 * This annotation can be applied at the class level.
 */
@Target({ElementType.TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = CompareStringsValidator.class)
@Documented
public @interface CompareStrings {
    /**
     * Specifies the names of the properties to be compared.
     *
     * @return an array of property names to be used for comparison
     */
    String[] propertyNames();

    /**
     * Defines the mode of comparison for string properties.
     *
     * @return the mode of comparison for string properties; defaults to EQUAL
     */
    StringComparisonMode matchMode() default EQUAL;

    /**
     * Indicates whether null values are permitted for string properties during validation.
     *
     * @return a boolean value; true if null values are allowed, false otherwise. Defaults to false.
     */
    boolean allowNull() default false;

    /**
     * Provides a default error message for constraint violations.
     *
     * @return the default error message
     */
    String message() default "";

    /**
     * Defines the groups the constraint belongs to.
     *
     * @return an array of classes that represent the groups the constraint is part of
     */
    Class<?>[] groups() default {};

    /**
     * Defines a list of payload classes that can be used by clients of the Bean Validation API to
     * assign custom payload objects to a constraint. This feature allows to associate additional
     * information with a constraint declaration.
     *
     * @return an array of payload classes
     */
    Class<? extends Payload>[] payload() default {};
}
