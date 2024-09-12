/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package test.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication()
public class ServerStarter {
    public static ConfigurableApplicationContext CTX;

    public static void main(String... args) {
        startServer1(args);
    }

    /**
     * Starts the server instance for ServerStarter using the specified configuration.
     *
     * @param args Command-line arguments passed to configure the application context.
     * @return ConfigurableApplicationContext instance for the server.
     */
    public static ConfigurableApplicationContext startServer1(String... args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        CTX = builder.sources(ServerStarter.class)
                     .properties("spring.config.location=classpath:/server1/application.properties")
                     .run(args);
        return CTX;
    }

    /**
     * Starts the server instance for ServerStarter using the specified configuration for server2.
     *
     * @param args Command-line arguments passed to configure the application context.
     * @return ConfigurableApplicationContext instance for the server.
     */
    public static ConfigurableApplicationContext startServer2(String... args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        return builder.sources(ServerStarter.class)
                      .properties(
                          "spring.config.location=classpath:/server2/application.properties")
                      .run(args);
    }

    /**
     * Starts the server instance for ServerStarter using the specified configuration for server3.
     *
     * @param args Command-line arguments passed to configure the application context.
     * @return ConfigurableApplicationContext instance for the server.
     */
    public static ConfigurableApplicationContext startServer3(String... args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        return builder.sources(ServerStarter.class)
                      .properties(
                          "spring.config.location=classpath:/server3/application.properties")
                      .run(args);
    }

    /**
     * Starts the server instance for ServerStarter using the specified configuration for server4.
     *
     * @param args Command-line arguments passed to configure the application context.
     * @return ConfigurableApplicationContext instance for the server.
     */
    public static ConfigurableApplicationContext startServer4(String... args) {
        var builder = new SpringApplicationBuilder();
        return builder.sources(ServerStarter.class)
                      .properties(
                          "spring.config.location=classpath:/server4/application.properties")
                      .run(args);
    }

    public static String getServerPort(ConfigurableApplicationContext ctx) {
        return ctx.getEnvironment().getProperty("local.server.port");
    }

    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {
        @Bean
        protected SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            return http
                .authorizeHttpRequests(auth ->
                                           auth.requestMatchers("/**").permitAll()
                                               .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .build();
        }
    }
}
