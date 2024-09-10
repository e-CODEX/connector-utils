/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.configuration.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import lombok.Data;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * The ConfigurationPropertyNode class represents nodes in a configuration tree structure. Each node
 * may have a name, an associated configuration property, and can contain child nodes.
 */
@Data
@SuppressWarnings("squid:S1135")
public class ConfigurationPropertyNode {
    private Map<String, ConfigurationPropertyNode> children = new HashMap<>();
    /**
     * The root node will have an empty string as name.
     */
    private String nodeName = "";
    @Nullable
    private ConfigurationProperty property;
    /**
     * This is the link to the parent node it is null if it is the root node.
     */
    @Nullable
    private ConfigurationPropertyNode parent;

    /**
     * Adds a child node to the current ConfigurationPropertyNode. The child node is added to the
     * internal children map, and its parent is set to the current node.
     *
     * @param propertyNode The child ConfigurationPropertyNode to be added.
     */
    public void addChild(ConfigurationPropertyNode propertyNode) {
        String name = propertyNode.getNodeName();
        this.children.put(name, propertyNode);
        propertyNode.setParent(this);
        // TODO: check property name ein propertyConfig
    }

    public Collection<ConfigurationPropertyNode> getChildren() {
        return this.children.values();
    }

    public ConfigurationPropertyNode removeChild(ConfigurationPropertyNode node) {
        return removeChild(node.getNodeName());
    }

    public ConfigurationPropertyNode removeChild(String name) {
        return this.children.remove(name);
    }

    @Nullable
    public ConfigurationProperty getProperty() {
        return property;
    }

    public void setProperty(@Nullable ConfigurationProperty property) {
        this.property = property;
    }

    /**
     * Constructs the full path of the node within the configuration tree. The full path is
     * represented as a dot-separated string hierarchy from the root node to the current node.
     *
     * @return The full node path as a dot-separated string.
     */
    public String getFullNodePath() {
        var nodePath = new StringBuilder();
        Stack<String> familyTree = new Stack<>();
        ConfigurationPropertyNode node = this;
        do {
            familyTree.push(node.getNodeName());
            node = node.getParent();
        } while (node != null && StringUtils.hasLength(node.getNodeName()));
        // pop empty root node
        if (!familyTree.empty()) {
            nodePath.append(familyTree.pop());
        }
        while (!familyTree.empty()) {
            var currentNode = familyTree.pop();
            nodePath.append(".").append(currentNode);
        }
        return nodePath.toString();
    }

    public boolean isRootNode() {
        return "".equals(nodeName);
    }

    public Optional<ConfigurationPropertyNode> getChild(String name) {
        return Optional.ofNullable(this.children.get(name));
    }
}
