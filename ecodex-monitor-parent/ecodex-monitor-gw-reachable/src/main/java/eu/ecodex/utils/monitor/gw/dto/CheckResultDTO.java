/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.gw.dto;

import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.Data;

/**
 * Data Transfer Object representing the result of a check operation.
 */
@Data
public class CheckResultDTO {
    String name;
    String message;
    String details;

    /**
     * Writes the stack trace of the given exception into the details of this CheckResultDTO.
     *
     * @param e the exception whose stack trace should be written into the details
     */
    public void writeStackTraceIntoDetails(Exception e) {
        var stringWriter = new StringWriter();
        var printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        this.setDetails(stringWriter.getBuffer().toString());
    }
}
