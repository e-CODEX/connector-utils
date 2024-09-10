/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.configuration.example1.validators;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A helper class that provides utility methods for constraint validation, such as extracting
 * property values, validating string collections based on comparison modes, and resolving
 * validation messages.
 */
@SuppressWarnings("squid:S1135")
public abstract class ConstraintValidatorHelper {
    /**
     * Retrieves the value of the specified property from the given object instance.
     *
     * @param <T>          the type of the property value
     * @param requiredType the required type of the property value; must not be null
     * @param propertyName the name of the property; must not be null
     * @param instance     the object instance from which to retrieve the property value; must not
     *                     be null
     * @return the value of the specified property, cast to the required type, or null if the
     *      property is not readable
     * @throws IllegalArgumentException if any of the required parameters are null or if the
     *                                  property is not defined in the instance class
     * @throws IllegalStateException    if the property is not readable
     */
    public static <T> T getPropertyValue(
        Class<T> requiredType, String propertyName, Object instance) {
        if (requiredType == null) {
            throw new IllegalArgumentException("Invalid argument. requiredType must NOT be null!");
        }
        if (propertyName == null) {
            throw new IllegalArgumentException("Invalid argument. PropertyName must NOT be null!");
        }
        if (instance == null) {
            throw new IllegalArgumentException(
                "Invalid argument. Object instance must NOT be null!");
        }
        T returnValue = null;
        try {
            var descriptor = new PropertyDescriptor(propertyName, instance.getClass());
            var readMethod = descriptor.getReadMethod();
            if (readMethod == null) {
                throw new IllegalStateException(
                    "Property '" + propertyName + "' of " + instance.getClass().getName()
                        + " is NOT readable!");
            }
            if (requiredType.isAssignableFrom(readMethod.getReturnType())) {
                try {
                    Object propertyValue = readMethod.invoke(instance);
                    returnValue = requiredType.cast(propertyValue);
                } catch (Exception e) {
                    e.printStackTrace(); // unable to invoke readMethod
                }
            }
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException(
                "Property '" + propertyName + "' is NOT defined in " + instance.getClass().getName()
                    + "!", e
            );
        }
        return returnValue;
    }

    /**
     * Validates a collection of string property values based on the specified comparison mode.
     *
     * @param propertyValues a collection of string property values to be validated
     * @param comparisonMode the mode of comparison, which determines how the values should be
     *                       compared
     * @return true if the collection of property values meets the specified comparison criteria;
     *      false otherwise
     */
    public static boolean isValid(
        Collection<String> propertyValues, StringComparisonMode comparisonMode) {
        boolean ignoreCase = switch (comparisonMode) {
            case EQUAL_IGNORE_CASE, NOT_EQUAL_IGNORE_CASE -> true;
            default -> false;
        };

        List<String> values = new ArrayList<>(propertyValues.size());
        for (String propertyValue : propertyValues) {
            if (ignoreCase) {
                values.add(propertyValue.toLowerCase());
            } else {
                values.add(propertyValue);
            }
        }

        return switch (comparisonMode) {
            case EQUAL, EQUAL_IGNORE_CASE -> {
                Set<String> uniqueValues = new HashSet<>(values);
                yield uniqueValues.size() == 1;
            }
            case NOT_EQUAL, NOT_EQUAL_IGNORE_CASE -> {
                Set<String> allValues = new HashSet<>(values);
                yield allValues.size() == values.size();
            }
        };
    }

    /**
     * Constructs a message based on the provided property names and the specified comparison mode.
     *
     * @param propertyNames  an array of property names to be included in the message
     * @param comparisonMode the mode of comparison which determines the message content
     * @return a constructed message string that indicates the comparison requirements of the
     *      properties
     */
    public static String resolveMessage(
        String[] propertyNames, StringComparisonMode comparisonMode) {
        var buffer = concatPropertyNames(propertyNames);
        buffer.append(" must");
        switch (comparisonMode) {
            case EQUAL, EQUAL_IGNORE_CASE:
                buffer.append(" be equal");
                break;
            case NOT_EQUAL, NOT_EQUAL_IGNORE_CASE:
                buffer.append(" not be equal");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + comparisonMode);
        }
        buffer.append('.');
        return buffer.toString();
    }

    private static StringBuffer concatPropertyNames(String[] propertyNames) {
        // TODO improve concatenating algorithm
        var buffer = new StringBuffer();
        buffer.append('[');
        for (String propertyName : propertyNames) {
            var firstChar = Character.toUpperCase(propertyName.charAt(0));
            buffer.append(firstChar);
            buffer.append(propertyName.substring(1));
            buffer.append(", ");
        }
        buffer.delete(buffer.length() - 2, buffer.length());
        buffer.append("]");
        return buffer;
    }
}
