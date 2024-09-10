/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.spring.quartz.configuration;

import static eu.ecodex.utils.spring.quartz.configuration.ScheduledWithQuartzConfiguration.TRIGGER_AND_JOB_DEFINITION_LIST_BEAN_NAME;

import eu.ecodex.utils.spring.quartz.annotation.QuartzScheduled;
import eu.ecodex.utils.spring.quartz.annotation.QuartzSchedules;
import eu.ecodex.utils.spring.quartz.domain.TriggerAndJobDefinition;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;

/**
 * The ScheduledBeanPostProcessor class is responsible for handling beans that are annotated with
 * the @QuartzScheduled annotation. It implements multiple interfaces to allow interaction with the
 * Spring container and various lifecycle phases of bean processing.
 */
@SuppressWarnings("squid:S1135")
public class ScheduledBeanPostProcessor
    implements DestructionAwareBeanPostProcessor, BeanFactoryAware, ApplicationContextAware {
    private static final Logger LOGGER = LogManager.getLogger(ScheduledBeanPostProcessor.class);
    private ApplicationContext applicationContext;
    private final Set<Class<?>> nonAnnotatedClasses =
        Collections.newSetFromMap(new ConcurrentHashMap<>(64));
    @Autowired
    @Qualifier(TRIGGER_AND_JOB_DEFINITION_LIST_BEAN_NAME)
    private List<TriggerAndJobDefinition> triggerAndJobDefinitionList;
    @Nullable
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void postProcessBeforeDestruction(Object o, String s) throws BeansException {
        // TODO see why the body is empty
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        if (this.beanFactory == null) {
            this.beanFactory = applicationContext;
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof AopInfrastructureBean || bean instanceof TaskScheduler
            || bean instanceof ScheduledExecutorService) {
            // Ignore AOP infrastructure such as scoped proxies.
            return bean;
        }

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (!this.nonAnnotatedClasses.contains(targetClass)) {
            Map<Method, Set<QuartzScheduled>> annotatedMethods = MethodIntrospector.selectMethods(
                targetClass,
                (MethodIntrospector.MetadataLookup<Set<QuartzScheduled>>) method -> {
                    Set<QuartzScheduled> quartzScheduledMethods =
                        AnnotatedElementUtils.getMergedRepeatableAnnotations(
                            method, QuartzScheduled.class, QuartzSchedules.class);
                    return (!quartzScheduledMethods.isEmpty() ? quartzScheduledMethods : null);
                }
            );
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(targetClass);
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace(
                        "No @Scheduled annotations found on bean class: {}", targetClass
                    );
                }
            } else {
                // Non-empty set of methods
                annotatedMethods.forEach((method, scheduledMethods) ->
                                             scheduledMethods.forEach(
                                                 quartzScheduled -> processScheduled(
                                                     beanName,
                                                     quartzScheduled,
                                                     method, bean
                                                 )));
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace(
                        "{} @Scheduled methods processed on bean '{}': {}",
                        annotatedMethods.size(), beanName, annotatedMethods
                    );
                }
            }
        }
        return bean;
    }

    private void processScheduled(
        String beanName, QuartzScheduled quartzScheduled, Method method, Object bean) {
        var definition = new TriggerAndJobDefinition(beanName, quartzScheduled, method, bean);
        LOGGER.debug("Adding definition [{}]", definition);
        triggerAndJobDefinitionList.add(definition);
    }
}
