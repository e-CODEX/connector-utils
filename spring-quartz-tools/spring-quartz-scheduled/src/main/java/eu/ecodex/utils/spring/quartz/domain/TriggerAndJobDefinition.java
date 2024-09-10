/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.spring.quartz.domain;

import eu.ecodex.utils.spring.quartz.annotation.QuartzScheduled;
import java.lang.reflect.Method;
import lombok.Data;
import org.springframework.core.style.ToStringCreator;

/**
 * The TriggerAndJobDefinition class encapsulates the essential details required for scheduling a
 * method execution within the Quartz scheduling framework. It includes properties for the bean
 * name, the QuartzScheduled annotation, method to be scheduled, and the bean instance itself.
 */
@Data
public class TriggerAndJobDefinition {
    private final String beanName;
    QuartzScheduled quartzScheduled;
    Method method;
    Object bean;

    /**
     * Constructor.
     *
     * @param beanName        the name of the bean containing the method to be scheduled
     * @param quartzScheduled an instance of the QuartzScheduled annotation to configure the
     *                        scheduling
     * @param method          the method to be scheduled
     * @param bean            the bean instance containing the method
     */
    public TriggerAndJobDefinition(
        String beanName, QuartzScheduled quartzScheduled, Method method, Object bean) {
        this.beanName = beanName;
        this.quartzScheduled = quartzScheduled;
        this.method = method;
        this.bean = bean;
    }

    public QuartzScheduled getScheduled() {
        return quartzScheduled;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
            .append("bean", this.beanName)
            .append("method", this.method.getName())
            .toString();
    }
}
