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

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.convert.converter.Converter;

/**
 * Converts a String to a Path object. It handles strings prefixed with "file://" or "file:",
 * removing the prefix before creating the Path object.
 */
public class PathConverter implements Converter<String, Path> {
    @Override
    public Path convert(String s) {

        if (s.startsWith("file://")) {
            s = s.substring("file://".length());
        }
        if (s.startsWith("file:")) {
            s = s.substring("file:".length());
        }
        return Paths.get(s);
    }
}
