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

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;

/**
 * This annotation is used to validate that a given resource is readable. It ensures that the
 * resource can be successfully opened and read.
 */
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ResourceReadableValidator.class)
@Documented
@NotNull
@SuppressWarnings("checkstyle:LineLength")
public @interface CheckResourceIsReadable {
    /**
     * Specifies the default error message if the resource is not readable.
     *
     * @return the default error message
     */
    String message() default "{eu.domibus.connector.lib.spring.configuration.validation.resource_input_stream_valid}";

    /**
     * Allows specification of validation groups to which this constraint belongs.
     *
     * @return an array of classes representing the validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Can be used by clients of the Bean Validation API to assign custom payload objects to a
     * constraint.
     *
     * @return an array of classes that extend Payload
     */
    Class<? extends Payload>[] payload() default {};
}
