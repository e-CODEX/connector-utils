/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.app;

import eu.ecodex.utils.spring.starter.SpringBootWarOnTomcatStarter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;

/**
 * Main configuration class for the Monitor application.
 *
 * <p>This class excludes the ActiveMQ autoconfiguration and initializes the application
 * with the appropriate configuration settings. It extends {@link SpringBootWarOnTomcatStarter}
 * to provide WAR deployment capabilities on a Tomcat server.
 *
 * <p>It overrides the {@code getSources} method to specify the primary sources for the
 * Spring application context.
 */
@SpringBootApplication(
    exclude = {ActiveMQAutoConfiguration.class}
)
public class MonitorAppConfiguration extends SpringBootWarOnTomcatStarter {
    @Override
    protected Class<?>[] getSources() {
        return new Class<?>[] {MonitorAppConfiguration.class};
    }
}
