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

import eu.ecodex.utils.spring.quartz.domain.TriggerAndJobDefinition;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.quartz.Scheduler;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.config.TaskManagementConfigUtils;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * This configuration class sets up the necessary beans and configurations for integrating
 * Quartz scheduling with Spring's task scheduling capabilities. It ensures that Quartz scheduler,
 * scheduler factory, and platform transaction manager are conditionally loaded into the
 * application context.
 */
@Configuration
@ConditionalOnClass({Scheduler.class, SchedulerFactoryBean.class, PlatformTransactionManager.class})
@AutoConfigureAfter(QuartzAutoConfiguration.class)
@ImportAutoConfiguration(QuartzAutoConfiguration.class)
public class ScheduledWithQuartzConfiguration {
    public static final String TRIGGER_AND_JOB_DEFINITION_LIST_BEAN_NAME =
        "eu.ecodex.utils.spring.quartz.configuration.TriggerAndJobDefinitionListBean";

    @Bean(name = TRIGGER_AND_JOB_DEFINITION_LIST_BEAN_NAME)
    List<TriggerAndJobDefinition> triggerAndJobDefinitionList() {
        return new CopyOnWriteArrayList<>();
    }

    @Bean
    @ConditionalOnMissingBean(
        name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME
    )
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    ScheduledBeanPostProcessor scheduledBeanPostProcessor() {
        return new ScheduledBeanPostProcessor();
    }

    @Bean
    TriggerAndJobDefinitionProcessor triggerAndJobDefinition() {
        return new TriggerAndJobDefinitionProcessor();
    }
}
