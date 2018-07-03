package com.myapp.security;

import com.myapp.utils.HttpUtils;

import javax.inject.Inject;
import javax.security.enterprise.identitystore.RememberMeIdentityStore;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.myapp.utils.CookiesConstants.JREMEMBERMEID;
import static com.myapp.utils.CookiesConstants.MAX_AGE_TO_REMOVE;

/**
 * <p>Created by MontolioV on 04.04.18.
 */
@WebServlet(value = "logout")
public class LogoutServlet extends HttpServlet {
    @Inject
    private RememberMeIdentityStore rememberMeIdentityStore;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie rmCookie = HttpUtils.findCookie(req, JREMEMBERMEID);
        if (rmCookie != null) {
            rememberMeIdentityStore.removeLoginToken(rmCookie.getValue());
            rmCookie.setMaxAge(MAX_AGE_TO_REMOVE);
            resp.addCookie(rmCookie);
        }
        req.logout();
        resp.sendRedirect(req.getContextPath());
    }

    public RememberMeIdentityStore getRememberMeIdentityStore() {
        return rememberMeIdentityStore;
    }

    public void setRememberMeIdentityStore(RememberMeIdentityStore rememberMeIdentityStore) {
        this.rememberMeIdentityStore = rememberMeIdentityStore;
    }
}
