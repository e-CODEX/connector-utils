/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.activemq.service;

import eu.ecodex.utils.monitor.activemq.config.ActiveMqEndpointConfigurationProperties;
import jakarta.jms.ConnectionFactory;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.remote.JMXServiceURL;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.web.BrokerFacade;
import org.apache.activemq.web.RemoteJMXBrokerFacade;
import org.apache.activemq.web.SingletonBrokerFacade;
import org.apache.activemq.web.config.WebConsoleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Factory class to create instances of {@link BrokerFacade}.
 *
 * <p>This class implements the {@link FactoryBean} interface to provide custom creation logic for
 * the BrokerFacade instances. It uses the {@link ActiveMqEndpointConfigurationProperties} to
 * determine the type of BrokerFacade to create and configure.
 */
public class BrokerFacadeFactory implements FactoryBean<BrokerFacade> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerFacadeFactory.class);
    @Autowired
    ActiveMqEndpointConfigurationProperties configurationProperties;
    BrokerFacade facade;

    @Override
    public BrokerFacade getObject() {
        if (facade == null) {
            initFacade();
        }
        return facade;
    }

    @Override
    public Class<?> getObjectType() {
        return BrokerFacade.class;
    }

    private void initFacade() {
        if (!configurationProperties.getJmxUrl().isEmpty()) {
            LOGGER.info("jmx url is present, creating RemoteJMXBrokerFacade");
            var brokerFacade = jmxBrokerFacade();
            brokerFacade.setBrokerName(configurationProperties.getBrokerName());
            facade = brokerFacade;
            return;
        }

        if (configurationProperties.isLocalJmx()) {
            LOGGER.info("local jmx is activated creating JmxBrokerFacade");
            var jmxBrokerFacade = new JmxLocalBrokerFacade();
            jmxBrokerFacade.setBrokerName(configurationProperties.getBrokerName());
            facade = jmxBrokerFacade;
            return;
        }

        LOGGER.info("Falling back to SingletonBrokerFacade");
        facade = new SingletonBrokerFacade();
    }

    private RemoteJMXBrokerFacade jmxBrokerFacade() {

        var remoteJMXBrokerFacade = new RemoteJMXBrokerFacade();
        remoteJMXBrokerFacade.setBrokerName("broker");
        remoteJMXBrokerFacade.setConfiguration(getWebConsoleConfiguration());

        return remoteJMXBrokerFacade;
    }

    private WebConsoleConfiguration getWebConsoleConfiguration() {

        return new WebConsoleConfiguration() {
            @Override
            public ConnectionFactory getConnectionFactory() {
                return null;
            }

            @Override
            public Collection<JMXServiceURL> getJmxUrls() {
                return configurationProperties.getJmxUrl();
            }

            @Override
            public String getJmxUser() {
                return configurationProperties.getJmxUser();
            }

            @Override
            public String getJmxPassword() {
                return configurationProperties.getJmxPassword();
            }
        };
    }

    private static class JmxLocalBrokerFacade extends RemoteJMXBrokerFacade {
        @Override
        public BrokerViewMBean getBrokerAdmin() throws Exception {
            var platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

            Set<ObjectName> brokers = findBrokers(platformMBeanServer);
            if (brokers.isEmpty()) {
                throw new IOException("No broker could be found in the JMX.");
            }
            ObjectName name = brokers.iterator().next();
            return MBeanServerInvocationHandler.newProxyInstance(
                platformMBeanServer,
                name,
                BrokerViewMBean.class,
                true
            );
        }

        @Override
        public Set queryNames(ObjectName name, QueryExp query) {
            var platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            return platformMBeanServer.queryNames(name, query);
        }

        @Override
        protected <T> Collection<T> getManagedObjects(ObjectName[] names, Class<T> type) {
            var platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

            List<T> answer = new ArrayList<>();
            if (platformMBeanServer != null) {
                for (ObjectName name : names) {
                    T value =
                        MBeanServerInvocationHandler.newProxyInstance(
                            platformMBeanServer,
                            name,
                            type,
                            true
                        );
                    if (value != null) {
                        answer.add(value);
                    }
                }
            }
            return answer;
        }
    }
}
