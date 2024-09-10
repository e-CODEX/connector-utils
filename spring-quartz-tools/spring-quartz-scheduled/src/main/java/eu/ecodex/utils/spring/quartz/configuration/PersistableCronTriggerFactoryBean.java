/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.spring.quartz.configuration;

import java.text.ParseException;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

/**
 * Needed to set Quartz useProperties=true when using Spring classes, because Spring sets an object
 * reference on JobDataMap that is not a String.
 *
 * @see <a
 * href="http://site.trimplement.com/using-spring-and-quartz-with-jobstore-properties">http://site.trimplement.com/using-spring-and-quartz-with-jobstore-properties/</a>
 * @see <a
 * href="http://forum.springsource.org/showthread.php?130984-Quartz-error-IOException">http://forum.springsource.org/showthread.php?130984-Quartz-error-IOException</a>
 */
public class PersistableCronTriggerFactoryBean extends CronTriggerFactoryBean {
    public static final String JOB_DETAIL_KEY = "jobDetail";

    @Override
    public void afterPropertiesSet() throws ParseException {
        super.afterPropertiesSet();
        // Remove the JobDetail element
        getJobDataMap().remove(JOB_DETAIL_KEY);
    }
}
