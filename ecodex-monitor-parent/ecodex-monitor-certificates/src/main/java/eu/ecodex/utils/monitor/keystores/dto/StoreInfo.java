/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.keystores.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.lang.Nullable;

/**
 * Class representing information about a cacert trust store.
 *
 * <pre>
 * This class holds details about a store such as its name, location, type, and access permissions,
 * as well as any messages and entries it may contain.
 * </pre>
 */
@Data
public class StoreInfo {
    String name;
    @Nullable
    String location;
    @Nullable
    String configuredLocation;
    @Nullable
    String type;
    @Nullable
    Boolean readable;
    @Nullable
    Boolean writeable;
    @Nullable
    String message;
    List<StoreEntryInfo> storeEntries = new ArrayList<>();
}
