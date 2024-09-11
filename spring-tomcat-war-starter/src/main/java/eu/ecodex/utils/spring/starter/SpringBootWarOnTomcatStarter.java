/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.spring.starter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Abstract class extending SpringBootServletInitializer to tailor Spring Boot applications for
 * deployment as WAR files on a Tomcat server.
 *
 * <p>The class provides custom configuration capabilities and defines abstract methods to be
 * implemented by subclasses for specifying application-specific sources and configurations.
 */
public abstract class SpringBootWarOnTomcatStarter extends SpringBootServletInitializer {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(SpringBootWarOnTomcatStarter.class);
    public static final String CATALINA_HOME = "catalina.home";
    public static final String SPRING_CONFIG_LOCATION = "spring.config.location";
    private String servletPath;

    /**
     * Runs the Spring Boot application with the given command-line arguments.
     *
     * @param args command-line arguments passed to the application
     */
    public void run(String[] args) {
        var builder = new SpringApplicationBuilder();
        var springProperties = new Properties();
        configureApplicationContext(builder, springProperties);
        builder.sources(getSources());
        builder.run(args);
    }

    private Path resolveCatalinaHome() {
        var catalinaHome = System.getProperty(CATALINA_HOME);
        Path currentRelativePath;
        if (catalinaHome != null) {
            currentRelativePath = Paths.get(catalinaHome);
            return currentRelativePath;
        }
        return null;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        var servletPath = servletContext.getContextPath();
        // remove leading / from servletPath: see javadoc of getContextPath for details
        servletPath = servletPath.substring(1);
        this.servletPath = servletPath;
        super.onStartup(servletContext);
    }

    @Override
    protected final SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        List<String> springConfigLocations = new ArrayList<>();

        springConfigLocations.add("classpath:/config/");
        springConfigLocations.add(String.format(
            "classpath:/config/%s/",
            getApplicationConfigLocationName()
        )); // look at relative directory config/<context>
        if (resolveCatalinaHome() != null) {
            var catalinaHomePath = resolveCatalinaHome();
            var catalinaPathConf = String.format(
                "file:%s/conf/%s/",
                catalinaHomePath.toAbsolutePath(),
                getApplicationConfigLocationName()
            );
            LOGGER.info(
                "CatalinaHome is set - adding [{}] to spring.config.location", catalinaPathConf
            );
            springConfigLocations.add(catalinaPathConf);

            var catalinaPathConfig = String.format(
                "file:%s/config/%s/",
                catalinaHomePath.toAbsolutePath(),
                getApplicationConfigLocationName()
            );
            LOGGER.info(
                "CatalinaHome is set - adding [{}] to spring.config.location", catalinaPathConfig
            );
            springConfigLocations.add(catalinaPathConfig);
        }

        if (System.getProperty(SPRING_CONFIG_LOCATION) == null) {
            var configLocations = String.join(",", springConfigLocations);
            LOGGER.info(
                "SystemProperty spring.config.location is not set - setting as "
                    + "spring.config.location: [{}]",
                configLocations
            );
            var springProperties = new Properties();
            springProperties.setProperty(SPRING_CONFIG_LOCATION, configLocations);
        } else {
            LOGGER.info(
                "SystemProperty spring.config.location is set - using spring.config.location={}",
                System.getProperty(SPRING_CONFIG_LOCATION)
            );
        }

        var springProperties = new Properties();
        springProperties.setProperty("spring.config.name", getConfigName());

        configureApplicationContext(application, springProperties);

        LOGGER.info("Using properties [{}] in spring application", springProperties);
        application.properties(springProperties);
        application.sources(getSources());
        return application;
    }

    protected void configureApplicationContext(
        SpringApplicationBuilder application, Properties springProperties) {
    }

    /**
     * Returns the directory name of the directory containing the spring properties.
     *
     * <p>By default if started within a servletContext, the servletContextName is used eg. if the
     * application is deployed on a tomcat under /app123 then this method will return app123
     *
     * @return the returned value will be used to configure spring.config.location
     */
    protected String getApplicationConfigLocationName() {
        if (this.servletPath == null) {
            return "";
        } else {
            return this.servletPath;
        }
    }

    /**
     * is used to set the spring property spring.config.name the default value is application so
     * spring boot will look for a application.properties file for loading the properties (or
     * application.yml if yml loading is available).
     *
     * <p>Can be overwritten to {@code protected String getConfigName() { return "foobar"; } }
     *
     * <p>Then spring.config.name will be set to foobar and spring boot will look for
     * foobar.properties
     *
     * @return the returned value will be set as spring.config.name
     */
    protected String getConfigName() {
        return "application";
    }

    /**
     * Must be overwritten.
     *
     * @return the returned classes are used as application source for spring
     */
    protected abstract Class<?>[] getSources();
}
