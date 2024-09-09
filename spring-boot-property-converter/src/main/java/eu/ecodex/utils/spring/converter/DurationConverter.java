/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.spring.converter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.core.convert.converter.Converter;

/**
 * Converts from a String to Duration by using the following pattern:
 * {@literal ^(?<number>\d+)(?<unit>(ms)|([s,m,h,d]))$}.
 */
public class DurationConverter implements Converter<String, Duration> {
    private static final String PATTERN = "^(?<number>\\d+)(?<unit>(ms)|([smhd]))$";
    private static final Map<String, TemporalUnit> UNIT_MAP =
        Stream.of(
                  new Object[] {"ms", ChronoUnit.MILLIS},
                  new Object[] {"s", ChronoUnit.SECONDS},
                  new Object[] {"m", ChronoUnit.MINUTES},
                  new Object[] {"h", ChronoUnit.HOURS},
                  new Object[] {"d", ChronoUnit.DAYS}
              )
              .collect(Collectors.toMap(
                  objects -> (String) objects[0],
                  objects -> (TemporalUnit) objects[1]
              ));
    private final Pattern compiledPattern;

    public DurationConverter() {
        this.compiledPattern = Pattern.compile(PATTERN);
    }

    @Override
    public Duration convert(String source) {
        var matcher = compiledPattern.matcher(source);
        if (matcher.matches()) {
            var number = matcher.group("number");
            var value = Long.parseLong(number);
            String unit = matcher.group("unit");
            var temporalUnit = UNIT_MAP.get(unit);
            return Duration.of(value, temporalUnit);
        }
        return Duration.parse(source);
    }
}
