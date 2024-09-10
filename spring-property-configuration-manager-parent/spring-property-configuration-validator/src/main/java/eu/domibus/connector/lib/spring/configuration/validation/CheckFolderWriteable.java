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
 * Annotation to validate that a given folder is writable. Can be used on methods, fields,
 * parameters, and annotation types.
 */
@Target({ANNOTATION_TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = FolderWriteableValidator.class)
@Documented
@NotNull
public @interface CheckFolderWriteable {
    /**
     * Specifies the default error message that will be used when the folder is not writable.
     *
     * @return the default error message "Cannot write to folder!"
     */
    String message() default "Cannot write to folder!";

    /**
     * Specifies validation groups to which this constraint belongs.
     *
     * @return an array of Class objects representing the validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Can be used to give additional information about the validation error.
     *
     * @return an array of Class objects extending Payload that provide additional metadata or
     *      information about the validation error
     */
    Class<? extends Payload>[] payload() default {};
}
