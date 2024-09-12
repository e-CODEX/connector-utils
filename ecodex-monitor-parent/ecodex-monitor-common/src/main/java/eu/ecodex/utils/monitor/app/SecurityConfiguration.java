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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfiguration class extends WebSecurityConfigurerAdapter to provide custom security
 * configurations for the application.
 *
 * <p>This configuration class specifically secures actuator endpoints, requiring users to have the
 * 'MONITOR' role to access them. It also configures HTTP Basic authentication for these endpoints
 * with a specified realm name "actuator".
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    protected SecurityFilterChain commonMonitorActuatorFilterChain(HttpSecurity http)
        throws Exception {
        return http
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/actuator/**");
                auth.anyRequest().hasAnyRole("MONITOR");
            })
            .httpBasic(basic -> basic.realmName("actuator"))
            .build();
    }
}
