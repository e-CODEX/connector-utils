/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.configuration.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 * Provides metadata for configuration properties, offering descriptions that enhance the
 * readability and understandability of configuration fields and types.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Inherited
@Documented
public @interface ConfigurationDescription {
    /**
     * An alias for the "value" attribute, providing a description for configuration fields
     * and types to improve readability and understandability.
     *
     * @return the description of the configuration field or type
     */
    @AliasFor("value") String description() default "";

    /**
     * An alias for the "description" attribute, providing a description for configuration fields
     * and types to improve readability and understandability.
     *
     * @return the description of the configuration field or type
     */
    @AliasFor("description") String value() default "";
}
