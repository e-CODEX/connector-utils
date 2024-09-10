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
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;

/**
 * This annotation is used to validate that a key store can be successfully loaded from the provided
 * path. It ensures the path is readable and the key store can be initialized correctly.
 */
@Target({TYPE, ANNOTATION_TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = StoreLoadableValidator.class)
@Documented
@NotNull
@SuppressWarnings("checkstyle:LineLength")
public @interface CheckStoreIsLoadable {
    /**
     * Default message for the constraint violation when the key store cannot be loaded.
     *
     * @return the default error message template
     */
    String message() default "{eu.domibus.connector.lib.spring.configuration.validation.cannot_load_key_store}";

    /**
     * Default groups for the constraint. This can be used to group related constraints.
     *
     * @return array of classes representing the groups
     */
    Class<?>[] groups() default {};

    /**
     * Specifies the payload for clients to specify additional information about the validation
     * failure. This attribute is primarily used to carry custom metadata information consumed
     * during validation error handling.
     *
     * @return an array of classes that extends Payload for custom metadata
     */
    Class<? extends Payload>[] payload() default {};
}
