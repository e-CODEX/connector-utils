/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.configuration.ui.vaadin.tools;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;

/**
 * A custom qualifier annotation to mark beans related to UI configuration property conversion
 * services.
 *
 * <p>This annotation is primarily used in the context of Spring's dependency injection to
 * distinguish between multiple possible {@link ConversionService} beans that could be present in
 * the application context. It helps in selecting and autowiring the UI-specific conversion service
 * where required.
 *
 * <p>The usage of this annotation ensures that the ConversionService configured with UI-specific
 * and default converters can be identified and injected into relevant Spring components or beans.
 *
 * @see Qualifier
 * @see org.springframework.beans.factory.annotation.Autowired
 * @see org.springframework.context.annotation.Bean
 * @see ConversionService
 */
@Qualifier(UiConfigurationConversationService.CONVERSION_SERVICE_QUALIFIER)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UiConfigurationConversationService {
    String CONVERSION_SERVICE_QUALIFIER = "CONFIGURATION_PROPERTY_UI_CONVERSION_SERVICE";
}
