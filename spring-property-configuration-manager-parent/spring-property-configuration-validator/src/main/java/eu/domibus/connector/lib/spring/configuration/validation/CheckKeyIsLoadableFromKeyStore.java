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
 * Annotation to ensure that a specific key is loadable from a configured key store. This validation
 * is used to verify that the key specified in the configuration can be successfully loaded from the
 * key store, ensuring that the configured key store and key details (such as alias and password)
 * are correct and functional.
 */
@Target({TYPE, ANNOTATION_TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Constraint(
    validatedBy = {KeyFromKeyStoreLoadableValidator.class, KeyFromKeyAndTrustStoreLoadable.class}
)
@Documented
@NotNull
public @interface CheckKeyIsLoadableFromKeyStore {
    /**
     * Provides the default error message for scenarios where a key cannot be loaded from the
     * configured key store.
     *
     * @return the default error message indicating that a key cannot be loaded from the key store
     */
    String message() default "Cannot load key from configured key store!";

    /**
     * Defines the group or groups the constraint belongs to for validation purposes.
     *
     * @return an array of classes representing the considered validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Defines payload for clients of the Bean Validation API. This attribute can be used by clients
     * to assign custom payload objects to a constraint, which can be utilized by a validation
     * client.
     *
     * @return an array of classes that extend Payload which can be utilized by validation clients
     *      to pass metadata information.
     */
    Class<? extends Payload>[] payload() default {};
}
