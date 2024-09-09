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

/**
 * The entry point for starting the Monitor application.
 *
 * <p>This class initializes the configuration for the Monitor application and
 * delegates the run process to the {@link MonitorAppConfiguration}.
 */
public class MonitorAppStarter {
    public static void main(String... args) {
        var monitorAppConfiguration = new MonitorAppConfiguration();
        monitorAppConfiguration.run(args);
    }
}
