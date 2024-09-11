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

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyPressEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.service.IUserPasswordService;
import eu.domibus.connector.web.auth.exception.InitialPasswordException;
import eu.domibus.connector.web.auth.exception.UserLoginException;
import eu.domibus.connector.web.layout.DashboardView;
import eu.domibus.connector.web.utils.ViewConstant;

/**
 * LoginDialog is a custom dialog for handling user authentication.
 */
@SuppressWarnings("squid:S1135")
// TODO: add configurable option for default redirect to VIEW
public class LoginDialog extends Dialog {
    Button loginButton = new Button("Login");

    /**
     * Constructor.
     *
     * @param userService the service responsible for handling user login and password operations
     */
    public LoginDialog(IUserPasswordService userService) {
        var username = new TextField();
        username.setLabel("Username");
        username.setAutofocus(true);
        username.addKeyPressListener(
            Key.ENTER,
            (ComponentEventListener<KeyPressEvent>) event -> loginButton.click()
        );

        var usernameDiv = new Div();
        usernameDiv.add(username);
        usernameDiv.getStyle().set(ViewConstant.ALIGNMENT_STYLE, ViewConstant.ALIGNMENT_CENTER);
        add(usernameDiv);

        var passwordDiv = new Div();
        var password = new PasswordField();
        password.setLabel("Password");
        password.addKeyPressListener(
            Key.ENTER,
            (ComponentEventListener<KeyPressEvent>) event -> loginButton.click()
        );
        passwordDiv.add(password);
        passwordDiv.getStyle().set(ViewConstant.ALIGNMENT_STYLE, ViewConstant.ALIGNMENT_CENTER);
        add(passwordDiv);

        var loginButtonContent = new Div();
        loginButtonContent.getStyle()
                          .set(ViewConstant.ALIGNMENT_STYLE, ViewConstant.ALIGNMENT_CENTER);
        loginButtonContent.getStyle().set("padding", "10px");

        loginButton.addClickListener(e -> {
            if (username.getValue().isEmpty()) {
                Dialog errorDialog =
                    new LoginErrorDialog("The field \"Username\" must not be empty!");
                username.clear();
                password.clear();
                errorDialog.open();
                return;
            }
            if (password.getValue().isEmpty()) {
                Dialog errorDialog =
                    new LoginErrorDialog("The field \"Password\" must not be empty!");
                password.clear();
                errorDialog.open();
                return;
            }
            try {
                userService.passwordLogin(username.getValue(), password.getValue());
            } catch (UserLoginException e1) {
                Dialog errorDialog = new LoginErrorDialog(e1.getMessage());
                username.clear();
                password.clear();
                errorDialog.open();
                return;
            } catch (InitialPasswordException e1) {
                username.clear();
                password.clear();
                close();

                var changePasswordDialog = new ChangePasswordDialog(
                    userService, username.getValue(), password.getValue()
                );
                changePasswordDialog.open();
            }
            this.getUI().ifPresent(ui -> ui.navigate(DashboardView.class));
            close();
        });
        loginButtonContent.add(loginButton);

        var changePasswordButton = new Button("Change Password");
        changePasswordButton.addClickListener(e -> {
            if (username.getValue().isEmpty()) {
                Dialog errorDialog =
                    new LoginErrorDialog("The field \"Username\" must not be empty!");
                username.clear();
                password.clear();
                errorDialog.open();
                return;
            }
            username.clear();
            password.clear();
            close();
            var changePasswordDialog = new ChangePasswordDialog(
                userService, username.getValue(), password.getValue()
            );
            changePasswordDialog.open();
        });
        loginButtonContent.add(changePasswordButton);

        add(loginButtonContent);
    }
}
