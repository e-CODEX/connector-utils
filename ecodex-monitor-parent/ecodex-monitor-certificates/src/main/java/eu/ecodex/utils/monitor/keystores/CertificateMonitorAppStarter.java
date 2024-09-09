/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.keystores;

import eu.ecodex.utils.spring.starter.SpringBootWarOnTomcatStarter;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Certificate Monitor application. This class is responsible for
 * initializing and running the Spring Boot application on a Tomcat server.
 *
 * <p>It extends {@link SpringBootWarOnTomcatStarter} to leverage the custom configurations and
 * methods provided for WAR deployment on a Tomcat server.
 */
@SpringBootApplication
public class CertificateMonitorAppStarter extends SpringBootWarOnTomcatStarter {
    /**
     * Main method that serves as the entry point for the Certificate Monitor application.
     * Initializes and runs the Spring Boot application on a Tomcat server.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String... args) {
        SpringBootWarOnTomcatStarter springBootWarOnTomcatStarter =
            new CertificateMonitorAppStarter();
        springBootWarOnTomcatStarter.run(args);
    }

    @Override
    protected Class<?>[] getSources() {
        return new Class<?>[] {CertificateMonitorAppStarter.class};
    }
}
