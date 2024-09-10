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

import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import eu.ecodex.utils.configuration.domain.ConfigurationPropertiesBean;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.domain.ConfigurationPropertyNode;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.validation.Validator;

/**
 * Implementation of the ConfigurationPropertyCollector interface. Responsible for collecting and
 * processing configuration properties from beans annotated with {@link ConfigurationProperties}.
 */
@SuppressWarnings("squid:S1135")
public class ConfigurationPropertyCollectorImpl implements ConfigurationPropertyCollector {
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationPropertyCollector.class);
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private Validator validator;

    public ConfigurationPropertyChecker getConfigChecker() {
        return new ConfigurationPropertyCheckerImpl(this, validator);
    }

    /**
     * returns a list of all configuration properties each ConfigurationProperty object holds the
     * configuration property key, optional if set a description and label name for further
     * information see {@link ConfigurationProperty}.
     *
     * @param basePackageFilter - the provided string is used as a filter, only with
     *                          {@link ConfigurationProperties} annotated classes under this package
     *                          path are scanned and returned
     * @return a list of ConfigurationProperty objects
     */
    @Override
    public Collection<ConfigurationProperty> getConfigurationProperties(
        String... basePackageFilter) {
        return getConfigurationProperties(Arrays.asList(basePackageFilter));
    }

    @Override
    public Collection<ConfigurationProperty> getConfigurationProperties(
        Class... basePackageClasses) {
        List<String> collect = Stream.of(basePackageClasses)
                                     .map(basePackageClass -> basePackageClass.getPackage()
                                                                              .getName())
                                     .collect(Collectors.toList());

        return this.getConfigurationProperties(collect);
    }

    private Collection<ConfigurationProperty> getConfigurationProperties(
        List<String> basePackageFilter) {
        return getConfigurationPropertiesMap(basePackageFilter).values();
    }

    @Override
    public ConfigurationPropertyNode getConfigurationPropertiesHirachie(
        String... basePackageFilter) {
        return getConfigurationPropertiesHirachie(Arrays.asList(basePackageFilter));
    }

    private ConfigurationPropertyNode getConfigurationPropertiesHirachie(List<String> asList) {
        var rootNode = new ConfigurationPropertyNode();
        this.getConfigurationBeans(asList)
            .forEach(
                clz -> this.getConfigurationPropertyHirachieFromClazz(new ArrayList<>(), rootNode,
                                                                      clz.getBeanClazz()
                ));
        return rootNode;
    }

    private Map<String, ConfigurationProperty> getConfigurationPropertiesMap(
        List<String> basePackageFilter) {
        List<ConfigurationPropertiesBean> configurationBeans =
            this.getConfigurationBeans(basePackageFilter);

        // filter out classes which aren't in package path basePackageFilter
        //                .filter(new PackageFilter(basePackageFilter))

        return configurationBeans
            .stream()
            .map(this::processBean)
            .map(List::stream)
            .flatMap(Function.identity())
            .collect(Collectors.toMap(ConfigurationProperty::getPropertyName, c -> c));
    }

    private List<ConfigurationProperty> processBean(ConfigurationPropertiesBean entry) {
        LOGGER.trace("processing config bean with name: [{}]", entry.getBeanName());
        Object bean = entry.getBean();
        Class<?> beanClazz = bean.getClass();

        return getConfigurationPropertyFromClazz(beanClazz);
    }

    @Override
    public List<ConfigurationProperty> getConfigurationPropertyFromClazz(Class<?> beanClass) {
        List<ConfigurationProperty> configList = new ArrayList<>();
        getConfigurationPropertyHirachieFromClazz(configList, null, beanClass);
        return configList;
    }

    private ConfigurationPropertyNode getConfigurationPropertyHirachieFromClazz(
        List<ConfigurationProperty> configList, ConfigurationPropertyNode rootNode,
        Class<?> beanClass) {
        if (!beanClass.isAnnotationPresent(ConfigurationProperties.class)) {
            throw new IllegalArgumentException(
                "Class must be annotated with " + ConfigurationProperties.class);
        }
        ConfigurationProperties configurationProperties =
            beanClass.getAnnotation(ConfigurationProperties.class);

        if (rootNode == null) {
            rootNode = new ConfigurationPropertyNode();
        }
        var prefix = configurationProperties.prefix();
        ConfigurationPropertyNode currentNode = rootNode;

        String[] split = prefix.split("\\.");
        for (String nodeName : split) {
            if (currentNode.getChild(nodeName).isPresent()) {
                currentNode = currentNode.getChild(nodeName).get();
            } else {
                var newNode = new ConfigurationPropertyNode();
                newNode.setNodeName(nodeName);
                currentNode.addChild(newNode);
                currentNode = newNode;
            }
        }

        processPropertyClazz(configList, currentNode, beanClass);
        return currentNode;
    }

    private void processPropertyClazz(
        List<ConfigurationProperty> configList, ConfigurationPropertyNode parent,
        Class<?> configurationClass) {
        List<Field> fields = collectDeclaredFields(
            configurationClass,
            new ArrayList<>()
        ); //.getDeclaredFields(); //TODO: also scan inherited fields...

        fields.stream()
              .filter(field -> {
                  var propertyDescriptor =
                      BeanUtils.getPropertyDescriptor(configurationClass, field.getName());
                  if (propertyDescriptor != null) {
                      return propertyDescriptor.getReadMethod() != null
                          && propertyDescriptor.getWriteMethod() != null;
                  }
                  return false;
              })
              .forEach(
                  field -> this.processFieldOfBean(configList, parent, configurationClass, field));
    }

    /**
     * Walks through parent classes and collects all declared fields.
     *
     * @param configurationClass - the class walk through
     * @return List of declared fields
     */
    private List<Field> collectDeclaredFields(Class<?> configurationClass, List<Field> fields) {
        if (configurationClass != null) {
            fields.addAll(Arrays.asList(configurationClass.getDeclaredFields()));
            collectDeclaredFields(configurationClass.getSuperclass(), fields);
        }
        return fields;
    }

    private void processFieldOfBean(
        List<ConfigurationProperty> configList, ConfigurationPropertyNode parent, Class parentClass,
        Field field) {
        LOGGER.trace("processing field [{}]", field);
        ConfigurationPropertyNode node;
        node = new ConfigurationPropertyNode();
        node.setNodeName(field.getName());
        parent.addChild(node);

        if (null != AnnotationUtils.getAnnotation(field, NestedConfigurationProperty.class)) {
            processPropertyClazz(configList, node, field.getType());
        } else {

            var configurationProperty = new ConfigurationProperty();
            configurationProperty.setPropertyName(node.getFullNodePath());
            configurationProperty.setBeanPropertyName(field.getName());
            configurationProperty.setParentClass(parentClass);
            configList.add(configurationProperty);
            node.setProperty(configurationProperty);

            ConfigurationDescription descriptionAnnotation =
                AnnotationUtils.getAnnotation(field, ConfigurationDescription.class);
            if (descriptionAnnotation != null) {
                String description =
                    (String) AnnotationUtils.getValue(descriptionAnnotation, "description");
                configurationProperty.setDescription(description);
            }

            ConfigurationLabel configLabelAnnotation =
                AnnotationUtils.getAnnotation(field, ConfigurationLabel.class);
            if (configLabelAnnotation != null) {
                String label = (String) AnnotationUtils.getValue(configLabelAnnotation);
                configurationProperty.setLabel(label);
            }

            DefaultValue defaultValueAnnotation =
                AnnotationUtils.getAnnotation(field, DefaultValue.class);
            if (defaultValueAnnotation != null) {
                configurationProperty.setDefaultValue(defaultValueAnnotation.value());
            }

            configurationProperty.setType(field.getType());
        }
    }

    @Override
    public List<ConfigurationPropertiesBean> getConfigurationBeans(List<String> basePackageFilter) {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ConfigurationProperties.class));

        // all @ConfigurationProperties bean definitions
        basePackageFilter
            .stream()
            .map(scanner::findCandidateComponents)
            .flatMap(Collection::stream)
            .toList();

        // TODO: check if active! @Profile + @ConditionalOnProperty

        Map<String, Object> configurationBeans =
            applicationContext.getBeansWithAnnotation(ConfigurationProperties.class);

        return configurationBeans
            .entrySet().stream()
            .filter(new PackageFilter(basePackageFilter))
            .map(this::processConfigurationPropertiesBean)
            .toList();
    }

    private ConfigurationPropertiesBean processConfigurationPropertiesBean(
        Map.Entry<String, Object> entry) {
        var propertiesBean = new ConfigurationPropertiesBean();

        Object bean = entry.getValue();
        Class<?> beanClass = bean.getClass();

        propertiesBean.setBeanName(entry.getKey());
        propertiesBean.setBeanClazz(beanClass);
        propertiesBean.setBean(bean);

        ConfigurationProperties configurationProperties =
            beanClass.getAnnotation(ConfigurationProperties.class);
        var configurationPrefix = configurationProperties.prefix();
        propertiesBean.setPropertyPrefix(configurationPrefix);

        ConfigurationLabel configLabelAnnotation =
            beanClass.getAnnotation(ConfigurationLabel.class);
        if (configLabelAnnotation != null) {
            String label = (String) AnnotationUtils.getValue(configLabelAnnotation);
            propertiesBean.setLabel(label);
        }

        ConfigurationDescription descriptionAnnotation =
            beanClass.getAnnotation(ConfigurationDescription.class);
        if (descriptionAnnotation != null) {
            String description = (String) AnnotationUtils.getValue(descriptionAnnotation);
            propertiesBean.setDescription(description);
        }

        return propertiesBean;
    }
}
