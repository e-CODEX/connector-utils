/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.web.login;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import eu.domibus.connector.web.utils.ViewConstant;

/**
 * LoginErrorDialog is a custom dialog component that displays an error message related to log in
 * issues.
 */
public class LoginErrorDialog extends Dialog {
    /**
     * LoginErrorDialog is a custom dialog component that displays an error message related to log
     * in issues.
     *
     * @param errorMessage the error message to be displayed on the dialog
     */
    public LoginErrorDialog(String errorMessage) {
        var loginExceptionDiv = new Div();
        var loginException = new NativeLabel(errorMessage);
        loginException.getStyle().set("font-weight", "bold");
        loginException.getStyle().set(ViewConstant.TAG_COLOR, ViewConstant.COLOR_RED);
        loginExceptionDiv.add(loginException);
        loginExceptionDiv.getStyle()
                         .set(ViewConstant.ALIGNMENT_STYLE, ViewConstant.ALIGNMENT_CENTER);
        loginExceptionDiv.setVisible(true);
        add(loginExceptionDiv);

        var okContent = new Div();
        okContent.getStyle().set(ViewConstant.ALIGNMENT_STYLE, ViewConstant.ALIGNMENT_CENTER);
        okContent.getStyle().set("padding", "10px");
        var okButton = new Button("OK");
        okButton.addClickListener(e2 -> close());
        okButton.setAutofocus(true);
        okContent.add(okButton);

        add(okContent);
    }
}
