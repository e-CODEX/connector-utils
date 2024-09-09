/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.activemq.service;

import eu.ecodex.utils.monitor.activemq.dto.DestinationInfo;
import java.util.ArrayList;
import java.util.List;
import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.broker.jmx.TopicViewMBean;
import org.apache.activemq.web.BrokerFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

/**
 * Exposes an Actuator endpoint to monitor ActiveMQ destinations such as queues and topics. It
 * provides operations to retrieve information about these destinations.
 */
@Endpoint(id = ActiveMqQueuesMonitorEndpoint.ENDPOINT_ID)
public class ActiveMqQueuesMonitorEndpoint {
    public static final String ENDPOINT_ID = "activemqdestinations";
    @Autowired
    BrokerFacade activeMqBrokerFacade;

    /**
     * Retrieves and maps the information about ActiveMQ destinations (queues and topics).
     *
     * @return a list of {@link DestinationInfo} objects containing the details of each destination.
     * @throws Exception if an error occurs while fetching the destination information.
     */
    @ReadOperation
    public List<DestinationInfo> getDestinationInfos() throws Exception {
        List<DestinationViewMBean> destinations = new ArrayList<>();

        destinations.addAll(activeMqBrokerFacade.getQueues());
        destinations.addAll(activeMqBrokerFacade.getTopics());

        return destinations.stream()
                           .map(this::mapToQueueInfo)
                           .toList();
    }

    private DestinationInfo mapToQueueInfo(DestinationViewMBean dst) {
        var info = new DestinationInfo();
        info.setName(dst.getName());

        info.setQueueSize(dst.getQueueSize());

        if (dst instanceof TopicViewMBean) {
            info.setType(DestinationInfo.DestinationType.TOPIC);
        } else if (dst instanceof QueueViewMBean) {
            info.setType(DestinationInfo.DestinationType.QUEUE);
        } else {
            info.setType(DestinationInfo.DestinationType.NOT_KNOWN);
        }

        info.setDequeueCount(dst.getDequeueCount());
        info.setDispatchCount(dst.getDispatchCount());
        info.setEnqueueCount(dst.getEnqueueCount());
        info.setMaxEnqueueTime(dst.getMaxEnqueueTime());
        info.setStoreMessageSize(dst.getStoreMessageSize());
        info.setMemoryLimit(dst.getMemoryLimit());
        info.setTempUsageLimit(dst.getTempUsageLimit());
        info.setMaxPageSize(dst.getMaxPageSize());

        return info;
    }
}
