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

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyPressEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.service.IUserPasswordService;
import eu.domibus.connector.web.auth.exception.InitialPasswordException;
import eu.domibus.connector.web.auth.exception.UserLoginException;
import eu.domibus.connector.web.layout.DashboardView;
import eu.domibus.connector.web.layout.DomibusConnectorAdminHeader;
import eu.domibus.connector.web.utils.ViewConstant;
import javax.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;

/**
 * LoginView is the primary login interface for the Domibus Connector Administration UI.
 */
@UIScope
@Route(value = LoginView.ROUTE)
@SpringComponent
@NoArgsConstructor
@SuppressWarnings("squid:S1135")
@PageTitle("domibusConnector - Login")
public class LoginView extends VerticalLayout
    implements HasUrlParameter<String>, BeforeEnterObserver {
    public static final String ROUTE = "login";
    public static final String PREVIOUS_ROUTE_PARAMETER = "afterLoginGoTo";
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private DomibusConnectorAdminHeader header;
    @Autowired
    IUserPasswordService userPasswordService;
    private final LoginOverlay login = new LoginOverlay();
    private final String afterLoginGoTo = DashboardView.ROUTE;
    private final TextField username = new TextField();
    private final PasswordField password = new PasswordField();

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (StringUtils.hasLength(parameter) && !LoginView.ROUTE.equals(parameter)) {
            // TODO see why the body is empty
        }
    }

    /**
     * Initializes the login view components and layout.
     */
    @PostConstruct
    public void init() {
        this.authenticationManager = authenticationManager;
        login.setAction("login");
        getElement().appendChild(login.getElement());

        add(new DomibusConnectorAdminHeader());

        var loginButton = new Button("Login");

        username.setLabel("Username");
        username.setAutofocus(true);
        username.addKeyPressListener(
            Key.ENTER,
            (ComponentEventListener<KeyPressEvent>) event -> loginButton.click()
        );

        var usernameDiv = new Div();
        usernameDiv.add(username);
        usernameDiv.getStyle().set(ViewConstant.ALIGNMENT_STYLE, ViewConstant.ALIGNMENT_CENTER);

        var loginArea = new VerticalLayout();
        loginArea.add(usernameDiv);

        var passwordDiv = new Div();

        password.setLabel("Password");
        password.addKeyPressListener(
            Key.ENTER,
            (ComponentEventListener<KeyPressEvent>) event -> loginButton.click()
        );
        passwordDiv.add(password);
        passwordDiv.getStyle().set(ViewConstant.ALIGNMENT_STYLE, ViewConstant.ALIGNMENT_CENTER);
        loginArea.add(passwordDiv);

        var loginButtonContent = new Div();
        loginButtonContent.getStyle()
                          .set(ViewConstant.ALIGNMENT_STYLE, ViewConstant.ALIGNMENT_CENTER);
        loginButtonContent.getStyle().set("padding", "10px");

        loginButton.addClickListener(this::loginButtonClicked);
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
            var changePasswordDialog = new ChangePasswordDialog(
                userPasswordService, username.getValue(), password.getValue()
            );
            username.clear();
            password.clear();
            changePasswordDialog.open();
        });
        loginButtonContent.add(changePasswordButton);

        loginArea.add(loginButtonContent);

        loginArea.setSizeFull();
        loginArea.setAlignItems(Alignment.CENTER);
        loginArea.getStyle().set("align-items", ViewConstant.ALIGNMENT_CENTER);

        var login = new HorizontalLayout();
        login.add(loginArea);
        login.setVerticalComponentAlignment(Alignment.CENTER, loginArea);

        add(loginArea);
    }

    private void loginButtonClicked(ClickEvent<Button> buttonClickEvent) {
        if (username.getValue().isEmpty()) {
            var errorDialog = new LoginErrorDialog("The field \"Username\" must not be empty!");
            username.clear();
            password.clear();
            errorDialog.open();
            return;
        }
        if (password.getValue().isEmpty()) {
            var errorDialog = new LoginErrorDialog("The field \"Password\" must not be empty!");
            password.clear();
            errorDialog.open();
            return;
        }
        try {
            userPasswordService.passwordLogin(username.getValue(), password.getValue());
        } catch (UserLoginException e1) {
            var errorDialog = new LoginErrorDialog(e1.getMessage());
            username.clear();
            password.clear();
            errorDialog.open();
            return;
        } catch (InitialPasswordException e1) {
            var changePasswordDialog =
                new ChangePasswordDialog(
                    userPasswordService, username.getValue(), password.getValue()
                );
            username.clear();
            password.clear();
            changePasswordDialog.open();
        } catch (AuthenticationException authException) {
            // show error message...
        }
        // TODO: navigate to previous route...
        //  getUI().ifPresent(ui -> ui);
        this.getUI().ifPresent(ui -> ui.navigate(afterLoginGoTo));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // TODO: check if current IUserPasswordService supports username/password login
        // if not redirect to login service...
        // IUserPasswordService
    }
}
