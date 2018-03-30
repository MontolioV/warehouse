package com.myapp.security;

import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import static javax.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;
import static javax.security.enterprise.identitystore.CredentialValidationResult.NOT_VALIDATED_RESULT;

/**
 * Workaround class to utilize app's mechanism for JavaEE8 security
 * until Wildfly implements JavaEE8 security.
 * <p>Created by MontolioV on 29.03.18.
 */
@Model
public class FormAuthenticationController {
    @Inject
    private CustomIdentityStore identityStore;
    @EJB
    private AccountStore accountStore;
    @Inject
    private FacesContext facesContext;
    private String login;
    private String password;
    private boolean rememberMe;

    public String submit() throws ServletException {
        CredentialValidationResult result = identityStore.validate(new UsernamePasswordCredential(login, password));
        // TODO: 29.03.18  Credential validation fails!
        if (result == null || result.equals(INVALID_RESULT) || result.equals(NOT_VALIDATED_RESULT)) {
            return "/login_error?faces-redirect=true";
        }

        Account account = accountStore.getAccountByLogin(login).get();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        httpServletRequest.login(account.getLogin(), account.getPassHash());
        return "/index?faces-redirect=true";
    }

    public CustomIdentityStore getIdentityStore() {
        return identityStore;
    }

    public void setIdentityStore(CustomIdentityStore identityStore) {
        this.identityStore = identityStore;
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
