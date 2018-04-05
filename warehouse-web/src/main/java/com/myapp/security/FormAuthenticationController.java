package com.myapp.security;

import com.myapp.utils.HttpUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.security.enterprise.CallerPrincipal;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.myapp.utils.CookiesConstants.JREMEMBERMEID;
import static com.myapp.utils.CookiesConstants.MAX_AGE_PARAM;
import static javax.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;
import static javax.security.enterprise.identitystore.CredentialValidationResult.NOT_VALIDATED_RESULT;

/**
 * Workaround class to utilize app's mechanism for JavaEE8 security
 * until Wildfly implements JavaEE8 security.
 * <p>Created by MontolioV on 29.03.18.
 */
@Model
public class FormAuthenticationController {
    public static final int REMEMBERME_MAX_AGE = 60 * 60 * 24 * 14;

    @Inject
    private RememberMeAuthenticator rmAuthenticator;
    @Inject
    private CustomIdentityStore identityStore;
    @Inject
    private CustomRememberMeIdentityStore rememberMeIdentityStore;
    @EJB
    private AccountStore accountStore;
    @Inject
    private FacesContext facesContext;
    private String login;
    private String password;
    private boolean rememberMe;
    private ExternalContext externalContext;
    private HttpServletRequest httpServletRequest;

    @PostConstruct
    public void init() {
        externalContext = facesContext.getExternalContext();
        httpServletRequest = (HttpServletRequest) externalContext.getRequest();
    }

    public String submit() throws ServletException {
        CredentialValidationResult result = identityStore.validate(new UsernamePasswordCredential(login, password));
        if (result == null || result.equals(INVALID_RESULT) || result.equals(NOT_VALIDATED_RESULT)) {
            return "/login_error?faces-redirect=true";
        }

        Account account = accountStore.getAccountByLogin(login).get();
        if (rememberMe) {
            Cookie rmCookie = HttpUtils.findCookie(httpServletRequest, JREMEMBERMEID);
            if (rmCookie != null) {
                rememberMeIdentityStore.removeLoginToken(rmCookie.getValue());
            }

            Map<String, Object> cookieProperties = new HashMap<>();
            cookieProperties.put(MAX_AGE_PARAM, REMEMBERME_MAX_AGE);
            Set<String> groups = new HashSet<>();
            account.getRoles().forEach(roles -> groups.add(roles.name()));
            String cookieValue = rememberMeIdentityStore.generateLoginToken(new CallerPrincipal(account.getLogin()), groups);
            externalContext.addResponseCookie(JREMEMBERMEID, cookieValue, cookieProperties);
        }

        httpServletRequest.logout();
        httpServletRequest.login(account.getLogin(), account.getPassHash());
        return "/index?faces-redirect=true";
    }

    public void checkCookie() throws ServletException, IOException {
        rmAuthenticator.cookieAuth(httpServletRequest);

        if (externalContext.getUserPrincipal() != null) {
            externalContext.redirect(httpServletRequest.getContextPath());
        }
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

    public RememberMeAuthenticator getRmAuthenticator() {
        return rmAuthenticator;
    }

    public void setRmAuthenticator(RememberMeAuthenticator rmAuthenticator) {
        this.rmAuthenticator = rmAuthenticator;
    }

    public CustomRememberMeIdentityStore getRememberMeIdentityStore() {
        return rememberMeIdentityStore;
    }

    public void setRememberMeIdentityStore(CustomRememberMeIdentityStore rememberMeIdentityStore) {
        this.rememberMeIdentityStore = rememberMeIdentityStore;
    }

    public AccountStore getAccountStore() {
        return accountStore;
    }

    public void setAccountStore(AccountStore accountStore) {
        this.accountStore = accountStore;
    }

    public FacesContext getFacesContext() {
        return facesContext;
    }

    public void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
    }
}
