/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.spring.quartz.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to group multiple {@link QuartzScheduled} annotations.
 *
 * <p>This meta-annotation allows for the specification of multiple
 * {@link QuartzScheduled} annotations on a single method or other annotation types.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QuartzSchedules {
    /**
     * An array of {@link QuartzScheduled} annotations.
     *
     * @return an array of {@link QuartzScheduled} annotations
     */
    QuartzScheduled[] value();
}
