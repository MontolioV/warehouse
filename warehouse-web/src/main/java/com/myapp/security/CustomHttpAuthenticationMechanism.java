package com.myapp.security;

import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.AutoApplySession;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.authentication.mechanism.http.RememberMe;
import javax.security.enterprise.credential.Credential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Intended for javax.security.enterprise. Not used for now.
 * <p>Created by MontolioV on 13.03.18.
 */
//@AutoApplySession
//@RememberMe(
//        cookieMaxAgeSeconds = 60 * 60 * 24 * 14,
//        cookieSecureOnly = false,
//        isRememberMeExpression = "#{self.isRememberMe(httpMessageContext)}"
//)
//@LoginToContinue(
//        loginPage = "/login.xhtml",
//        errorPage = "/login-error.xhtml"
//)
//@ApplicationScoped
public class CustomHttpAuthenticationMechanism implements HttpAuthenticationMechanism {
    @Inject
    private CustomIdentityStore identityStore;

    /**
     * Authenticate an HTTP request.
     * <p>
     * <p>
     * This method is called in response to an HTTP client request for a resource, and is always invoked
     * <strong>before</strong> any {@link Filter} or {@link HttpServlet}. Additionally this method is called
     * in response to {@link HttpServletRequest#authenticate(HttpServletResponse)}
     * <p>
     * <p>
     * Note that by default this method is <strong>always</strong> called for every request, independent of whether
     * the request is to a protected or non-protected resource, or whether a caller was successfully authenticated
     * before within the same HTTP session or not.
     * <p>
     * <p>
     * A CDI/Interceptor spec interceptor can be used to prevent calls to this method if needed.
     * See {@link AutoApplySession} and {@link RememberMe} for two examples.
     *
     * @param request            contains the request the client has made
     * @param response           contains the response that will be send to the client
     * @param httpMessageContext context for interacting with the container
     * @return the completion status of the processing performed by this method
     * @throws AuthenticationException when the processing failed
     */
    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {
        Credential credential = httpMessageContext.getAuthParameters().getCredential();
        if (credential != null) {
            return httpMessageContext.notifyContainerAboutLogin(identityStore.validate(credential));
        } else {
            return httpMessageContext.doNothing();
        }
    }

    public boolean isRememberMe(HttpMessageContext context) {
        return context.getAuthParameters().isRememberMe();
    }

    public CustomIdentityStore getIdentityStore() {
        return identityStore;
    }

    public void setIdentityStore(CustomIdentityStore identityStore) {
        this.identityStore = identityStore;
    }
}
