package com.myapp.security;

import com.myapp.utils.HttpUtils;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.myapp.utils.CookiesConstants.JREMEMBERMEID;

/**
 * <p>Created by MontolioV on 05.04.18.
 */
@ApplicationScoped
public class RememberMeAuthenticator {
    @Inject
    private HttpUtils httpUtils;
    @EJB
    private AccountStore accountStore;

    public void cookieAuth(HttpServletRequest req) throws ServletException {
        if (req.getUserPrincipal() == null) {
            Cookie rmCookie = httpUtils.findCookie(req, JREMEMBERMEID);
            if (rmCookie != null) {
                Optional<Account> accountByTokenHash = accountStore.getAccountByTokenHash(rmCookie.getValue());
                if (accountByTokenHash.isPresent()) {
                    req.login(accountByTokenHash.get().getLogin(), accountByTokenHash.get().getPassHash());
                }
            }
        }
    }
}
