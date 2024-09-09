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

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;
import org.assertj.core.api.AbstractObjectAssert;

/**
 * MemoryAppenderAssert is a custom assertion class for validating various conditions on
 * MemoryAppender instances.
 */
public class MemoryAppenderAssert
    extends AbstractObjectAssert<MemoryAppenderAssert, MemoryAppender> {
    private Stream<LogEvent> stream;

    /**
     * Filters the log events based on the specified marker name.
     *
     * @param markerName the name of the marker to filter log events by
     * @return the updated MemoryAppenderAssert instance with the filtered stream
     */
    public MemoryAppenderAssert filterOnMarker(String markerName) {
        this.stream = getStream().filter(
            e -> e.getMarker() != null && Objects.equals(e.getMarker().getName(), markerName));
        return this;
    }

    public MemoryAppenderAssert filterOnLogLevel(Level logLevel) {
        this.stream = getStream().filter(e -> e.getLevel().equals(logLevel));
        return this;
    }

    public MemoryAppenderAssert filterOn(Predicate<LogEvent> predicate) {
        this.stream = getStream().filter(predicate);
        return this;
    }

    /**
     * Asserts that the log contains at least one log message that includes the specified log line.
     *
     * @param logLine the log line to be searched within the log messages
     * @return the updated MemoryAppenderAssert instance if the log line is found
     * @throws AssertionError if no log message contains the specified log line
     */
    public MemoryAppenderAssert containsLogLine(String logLine) {
        final String lowerCaseLogLine = logLine.toLowerCase();
        if (!getStream()
            .map(LogEvent::getMessage)
            .anyMatch(
                (Message m) -> m.getFormattedMessage().toLowerCase().contains(lowerCaseLogLine))) {
            failWithMessage("No log message found with logLine <%s>", lowerCaseLogLine);
        }
        return this;
    }

    /**
     * Asserts that the number of log lines in the memory appender matches the expected number.
     *
     * @param number the expected number of log lines
     * @return the updated MemoryAppenderAssert instance if the number of log lines matches
     * @throws AssertionError if the number of log lines does not match the expected number
     */
    public MemoryAppenderAssert hasLogLines(long number) {
        long count = getStream().count();
        if (count != number) {
            failWithMessage("The number of log lines was <%d> instead of <%d>", count, number);
        }
        return this;
    }

    private Stream<LogEvent> getStream() {
        if (this.stream != null) {
            return stream;
        }
        isNotNull();
        Stream<LogEvent> stream = actual.getLogEventList().stream();
        this.stream = stream;
        return stream;
    }

    public MemoryAppenderAssert(MemoryAppender memoryAppender) {
        super(memoryAppender, MemoryAppenderAssert.class);
    }

    public static MemoryAppenderAssert assertThat(MemoryAppender actual) {
        return new MemoryAppenderAssert(actual);
    }
}
