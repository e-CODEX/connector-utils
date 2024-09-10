/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.configuration.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * The PackageFilter class is a predicate implementation used to filter map entries based on the
 * package name of the value's class. It checks if the package of the class starts with any of the
 * specified base package filters.
 */
public class PackageFilter implements Predicate<Map.Entry<String, Object>> {
    private final List<String> basePackageFilter;

    public PackageFilter(String... basePackageFilter) {
        this.basePackageFilter = Arrays.asList(basePackageFilter);
    }

    public PackageFilter(List<String> basePackageFilter) {
        this.basePackageFilter = basePackageFilter;
    }

    @Override
    public boolean test(Map.Entry<String, Object> entry) {
        if (this.basePackageFilter == null || this.basePackageFilter.isEmpty()) {
            return true;
        }
        return basePackageFilter
            .stream()
            .anyMatch(
                filter -> entry.getValue().getClass().getPackage().getName().startsWith(filter));
    }
}



