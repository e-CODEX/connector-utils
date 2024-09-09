/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.test.logging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * MemoryAppender is a custom appender for logging frameworks that stores log events in memory. This
 * implementation provides a way to capture log events in a synchronized list which can be useful
 * for testing or in-memory analysis of log data.
 */
@Plugin(name = "MemoryAppender", category = "Core", elementType = "appender", printObject = true)
public class MemoryAppender extends AbstractAppender {
    private List<LogEvent> logEventList = Collections.synchronizedList(new ArrayList<>());

    protected MemoryAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout);
    }

    @Override
    public void append(LogEvent logEvent) {
        logEventList.add(logEvent.toImmutable());
    }

    public List<LogEvent> getLogEventList() {
        return this.logEventList;
    }

    public void reset() {
        logEventList = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Retrieves the instance of the MemoryAppender from the current LoggerContext configuration.
     *
     * @return the instance of MemoryAppender configured for the application, or null if no such
     *      appender is configured.
     */
    public static MemoryAppender getAppender() {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final var config = ctx.getConfiguration();

        return config.getAppender("Memory");
    }

    /**
     * Creates a new instance of MemoryAppender with the specified attributes.
     *
     * @param name The name of the appender.
     * @param layout The layout to use for formatting log events.
     * @param filter The filter to use to filter log events.
     * @return A new MemoryAppender instance or null if the name is not provided.
     */
    @PluginFactory
    public static MemoryAppender createAppender(
        @PluginAttribute("name") String name,
        @PluginElement("Layout") Layout<? extends Serializable> layout,
        @PluginElement("Filter") final Filter filter) {
        if (name == null) {
            LOGGER.error("No name provided for MyCustomAppenderImpl");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new MemoryAppender(name, filter, layout);
    }
}
