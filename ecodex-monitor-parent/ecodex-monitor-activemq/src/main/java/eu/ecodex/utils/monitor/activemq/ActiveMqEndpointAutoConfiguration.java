/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.activemq;

import eu.ecodex.utils.monitor.activemq.config.ActiveMqEndpointConfigurationProperties;
import eu.ecodex.utils.monitor.activemq.config.ActiveMqHealthChecksConfigurationProperties;
import eu.ecodex.utils.monitor.activemq.config.ActiveMqMetricConfigurationProperties;
import eu.ecodex.utils.monitor.activemq.service.ActiveMqHealthService;
import eu.ecodex.utils.monitor.activemq.service.ActiveMqMetricService;
import eu.ecodex.utils.monitor.activemq.service.ActiveMqQueuesMonitorEndpoint;
import eu.ecodex.utils.monitor.activemq.service.BrokerFacadeFactory;
import eu.ecodex.utils.monitor.activemq.service.DestinationService;
import io.micrometer.core.instrument.util.StringUtils;
import java.util.Optional;
import org.apache.activemq.web.BrokerFacade;
import org.apache.activemq.web.SingletonBrokerFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Autoconfiguration for ActiveMQ endpoint monitoring and related services.
 *
 * <p>This class configures the necessary beans and settings for monitoring ActiveMQ brokers,
 * collecting metrics, and performing health checks. It reads configuration properties related to
 * ActiveMQ monitoring and conditionally enables or disables features based on these properties.
 */
@Configuration
@EnableConfigurationProperties(ActiveMqEndpointConfigurationProperties.class)
@ConditionalOnProperty(
    prefix = ActiveMqEndpointConfigurationProperties.ACTIVEMQ_MONITOR_PREFIX, name = "enabled",
    havingValue = "true"
)
@ComponentScan(basePackageClasses = ActiveMqEndpointAutoConfiguration.class)
public class ActiveMqEndpointAutoConfiguration {
    /**
     * Configuration class for setting up ActiveMQ metrics monitoring.
     *
     * <p>This class is conditionally loaded when the property specified by
     * ActiveMqMetricConfigurationProperties.PREFIX is enabled. It initializes and provides an
     * ActiveMqMetricService as a Spring bean.
     */
    @Configuration
    @ConditionalOnProperty(
        prefix = ActiveMqMetricConfigurationProperties.PREFIX, name = "enabled",
        havingValue = "true"
    )
    @EnableConfigurationProperties(ActiveMqMetricConfigurationProperties.class)
    public static class MetricConfiguration {
        @Bean
        @Lazy(false)
        ActiveMqMetricService activeMqMetricService() {
            return new ActiveMqMetricService();
        }
    }

    /**
     * Configuration class for setting up ActiveMQ health checks.
     *
     * <p>This class is responsible for configuring the necessary beans and settings required to
     * perform health checks on ActiveMQ queues. It relies on the
     * ActiveMqHealthChecksConfigurationProperties to determine if the health checks should be
     * enabled. If enabled, it creates and registers an ActiveMqHealthService bean.
     *
     * <p>An instance of `ActiveMqHealthService` is created as a bean within this configuration
     * class to allow the application to monitor the health status of ActiveMQ destinations.
     */
    @Configuration
    @ConditionalOnProperty(
        prefix = ActiveMqHealthChecksConfigurationProperties.PREFIX, name = "enabled",
        havingValue = "true"
    )
    @EnableConfigurationProperties(ActiveMqHealthChecksConfigurationProperties.class)
    public static class HealthConfiguration {
        @Bean
        @Lazy(false)
        ActiveMqHealthService activeMqHealthService() {
            return new ActiveMqHealthService();
        }
    }

    @Autowired
    ActiveMqEndpointConfigurationProperties configurationProperties;

    @Bean
    ActiveMqQueuesMonitorEndpoint monitorEndpoint() {
        return new ActiveMqQueuesMonitorEndpoint();
    }

    @Bean
    DestinationService destinationService() {
        return new DestinationService();
    }

    @Bean
    BrokerFacadeFactory brokerFacadeFactory() {
        return new BrokerFacadeFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    BrokerFacade localBrokerFacade() {
        return new SingletonBrokerFacade();
    }

    /**
     * Abstract base class for condition evaluation of ActiveMQ endpoint configuration properties.
     *
     * <p>This class implements the Condition interface and provides a method to retrieve
     * an instance of {@link ActiveMqEndpointConfigurationProperties} from the application context.
     * Subclasses should implement the {@link Condition#matches} method to define their specific
     * condition logic.
     */
    public abstract static class ActiveMqEndpointConfigurationPropertiesCondition
        implements Condition {
        Optional<ActiveMqEndpointConfigurationProperties> getProps(ConditionContext context) {
            Bindable<ActiveMqEndpointConfigurationProperties> bindable =
                Bindable.of(ActiveMqEndpointConfigurationProperties.class);
            var binder = Binder.get(context.getEnvironment());
            BindResult<ActiveMqEndpointConfigurationProperties> bindResult =
                binder.bind(ActiveMqEndpointConfigurationProperties.ACTIVEMQ_MONITOR_PREFIX,
                            bindable, null
                );
            return Optional.ofNullable(bindResult.orElse(null));
        }
    }

    /**
     * Condition that checks if the JMX URL list is not empty in the
     * ActiveMqEndpointConfigurationProperties.
     *
     * <p>This condition class extends ActiveMqEndpointConfigurationPropertiesCondition and
     * implements the logic to verify that the JMX URL list is not empty. This can be used to
     * conditionally enable or disable certain beans or configurations based on the presence of JMX
     * URLs.
     */
    public static class JmxUrlNotEmptyCondition
        extends ActiveMqEndpointConfigurationPropertiesCondition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Optional<ActiveMqEndpointConfigurationProperties> bean = getProps(context);
            return bean.isPresent() && !bean.get().getJmxUrl().isEmpty();
        }
    }

    /**
     * Condition class that checks if the broker name is not empty.
     *
     * <p>This condition will return true if an instance of
     * {@link ActiveMqEndpointConfigurationProperties} is present in the application context and its
     * broker name is not empty. This can be used to conditionally enable certain beans based on
     * whether the broker name is provided.
     */
    public static class BrokerNameNotEmptyCondition
        extends ActiveMqEndpointConfigurationPropertiesCondition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Optional<ActiveMqEndpointConfigurationProperties> bean = getProps(context);
            return bean.isPresent() && StringUtils.isNotEmpty(bean.get().getBrokerName());
        }
    }
}
