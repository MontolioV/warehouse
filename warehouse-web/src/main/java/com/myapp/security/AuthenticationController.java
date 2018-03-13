package com.myapp.security;

import org.omnifaces.cdi.Param;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;

import static javax.security.enterprise.AuthenticationStatus.SEND_FAILURE;
import static javax.security.enterprise.AuthenticationStatus.SUCCESS;
import static javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters.withParams;
import static org.omnifaces.util.Faces.*;
import static org.omnifaces.util.Messages.addGlobalError;

/**
 * <p>Created by MontolioV on 13.03.18.
 */
@Model
public class AuthenticationController {
    @Inject
    private SecurityContext securityContext;
    @Inject
    @Param(name = "continue")
    private boolean loginToContinue;
    private String login;
    private String password;
    private boolean rememberMe;

    public void submit() {
        Credential credential = new UsernamePasswordCredential(login, password);
        AuthenticationStatus status = securityContext.authenticate(
                getRequest(),
                getResponse(),
                withParams()
                        .credential(credential)
                        .newAuthentication(!loginToContinue)
                        .rememberMe(rememberMe));
        if (status.equals(SUCCESS)) {
            redirect("index.xhtml");
        } else if (status.equals(SEND_FAILURE)) {
            addGlobalError("auth.message.error.failure");
            validationFailed();
        }
    }

    public SecurityContext getSecurityContext() {
        return securityContext;
    }

    public void setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    public boolean isLoginToContinue() {
        return loginToContinue;
    }

    public void setLoginToContinue(boolean loginToContinue) {
        this.loginToContinue = loginToContinue;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
