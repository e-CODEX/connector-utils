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

import eu.ecodex.utils.spring.starter.SpringBootWarOnTomcatStarter;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for starting the ActiveMQ Monitor application as a Spring Boot application on Tomcat.
 * This class extends {@code SpringBootWarOnTomcatStarter}, leveraging its functionality to
 * configure and run the application within a servlet container.
 */
@SpringBootApplication
public class ActiveMqMonitorAppStarter extends SpringBootWarOnTomcatStarter {
    public static void main(String... args) {
        SpringBootWarOnTomcatStarter springBootWarOnTomcatStarter = new ActiveMqMonitorAppStarter();
        springBootWarOnTomcatStarter.run(args);
    }

    @Override
    protected Class<?>[] getSources() {
        return new Class<?>[] {ActiveMqMonitorAppStarter.class};
    }
}
