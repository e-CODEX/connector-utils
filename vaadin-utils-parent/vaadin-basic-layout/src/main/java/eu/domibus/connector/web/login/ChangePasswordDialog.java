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
import com.vaadin.flow.component.textfield.PasswordField;
import eu.domibus.connector.service.IUserPasswordService;
import eu.domibus.connector.web.auth.exception.UserLoginException;
import eu.domibus.connector.web.layout.DashboardView;
import eu.domibus.connector.web.utils.ViewConstant;

/**
 * ChangePasswordDialog is a dialog that facilitates the process of changing a user's password.
 */
public class ChangePasswordDialog extends Dialog {
    /**
     * Constructor.
     *
     * @param userPasswordService The user password service responsible for handling password
     *                            changes.
     * @param username            The username of the user requesting the password change.
     * @param password            The current password of the user.
     */
    public ChangePasswordDialog(
        IUserPasswordService userPasswordService, String username, String password) {
        var changePasswordDiv = new Div();
        var changePassword = new NativeLabel("Change Password for User " + username);
        changePassword.getStyle().set("font-weight", "bold");
        changePasswordDiv.add(changePassword);
        changePasswordDiv.getStyle()
                         .set(ViewConstant.ALIGNMENT_STYLE, ViewConstant.ALIGNMENT_CENTER);
        changePasswordDiv.setVisible(true);
        add(changePasswordDiv);

        var changePassword2Div = new Div();
        var changePassword2 = new NativeLabel("Your password must be changed.");
        changePassword2Div.add(changePassword2);
        changePassword2Div.getStyle()
                          .set(ViewConstant.ALIGNMENT_STYLE, ViewConstant.ALIGNMENT_CENTER);
        changePassword2Div.setVisible(true);
        add(changePassword2Div);

        var currentPwDiv = new Div();
        var currentPw = new PasswordField();
        currentPw.setLabel("Current Password:");
        currentPw.setValue(password);
        currentPwDiv.add(currentPw);
        currentPwDiv.getStyle().set(ViewConstant.ALIGNMENT_STYLE, ViewConstant.ALIGNMENT_CENTER);
        add(currentPwDiv);

        var newPwDiv = new Div();
        var newPw = new PasswordField();
        newPw.setLabel("New Password:");
        newPwDiv.add(newPw);
        newPwDiv.getStyle().set(ViewConstant.ALIGNMENT_STYLE, ViewConstant.ALIGNMENT_CENTER);
        add(newPwDiv);

        var confirmNewPwDiv = new Div();
        var confirmNewPw = new PasswordField();
        confirmNewPw.setLabel("Confirm new Password:");
        confirmNewPwDiv.add(confirmNewPw);
        confirmNewPwDiv.getStyle().set(ViewConstant.ALIGNMENT_STYLE, ViewConstant.ALIGNMENT_CENTER);
        add(confirmNewPwDiv);

        var changePasswordButtonContent = new Div();
        changePasswordButtonContent.getStyle().set(ViewConstant.ALIGNMENT_STYLE,
                                                   ViewConstant.ALIGNMENT_CENTER
        );
        changePasswordButtonContent.getStyle().set("padding", "10px");
        var changePasswordButton = new Button("Change Password");
        changePasswordButton.addClickListener(e -> {
            if (currentPw.isEmpty()) {
                Dialog errorDialog =
                    new LoginErrorDialog("The Field 'Current Password' must not be empty!");
                errorDialog.open();
                return;
            }
            if (newPw.isEmpty()) {
                Dialog errorDialog =
                    new LoginErrorDialog("The Field 'New Password' must not be empty!");
                errorDialog.open();
                return;
            }
            if (confirmNewPw.isEmpty()) {
                Dialog errorDialog =
                    new LoginErrorDialog("The Field 'Confirm new Password' must not be empty!");
                errorDialog.open();
                return;
            }
            if (!newPw.getValue().equals(confirmNewPw.getValue())) {
                Dialog errorDialog = new LoginErrorDialog(
                    "The Fields 'New Password' and 'Confirm new Password' must have "
                        + "the same values!"
                );
                newPw.clear();
                confirmNewPw.clear();
                errorDialog.open();
                return;
            }

            try {
                userPasswordService.changePasswordLogin(
                    username, currentPw.getValue(), newPw.getValue());
            } catch (UserLoginException e1) {
                currentPw.clear();
                newPw.clear();
                confirmNewPw.clear();

                var errorDialog = new LoginErrorDialog(e1.getMessage());
                errorDialog.open();
                return;
            }
            this.getUI().ifPresent(ui -> ui.navigate(DashboardView.class));
            close();
        });
        changePasswordButtonContent.add(changePasswordButton);

        add(changePasswordButtonContent);
    }
}
