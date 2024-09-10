/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.configuration.annotation.processor;

import org.springframework.boot.configurationprocessor.ConfigurationMetadataAnnotationProcessor;

/**
 * AnnotatationMetadataProcessor is an extension of ConfigurationMetadataAnnotationProcessor. This
 * processor is designed to add ConfigurationDescription and ConfigurationLabel annotations to the
 * metadata .json file.
 *
 * <p>This class should be extended to implement the required functionality for processing
 * additional annotations and updating the metadata accordingly.
 */
@SuppressWarnings("squid:S1135")
// TODO: extend this annotation processor to add ConfigurationDescripion and ConfigurationLabel
//  annotations
// to the metadata .json file
public class AnnotatationMetadataProcessor extends ConfigurationMetadataAnnotationProcessor {
}
