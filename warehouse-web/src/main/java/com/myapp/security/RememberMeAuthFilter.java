package com.myapp.security;

import com.myapp.utils.HttpUtils;

import javax.ejb.EJB;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.myapp.utils.CookiesConstants.JREMEMBERMEID;

/**
 * <p>Created by MontolioV on 04.04.18.
 */
@WebFilter(value = "/*")
public class RememberMeAuthFilter extends HttpFilter {
    @EJB
    private AccountStore accountStore;

    /**
     * <p>The <code>doFilter</code> method of the Filter is called by the
     * container each time a request/response pair is passed through the
     * chain due to a client request for a resource at the end of the chain.
     * The FilterChain passed in to this method allows the Filter to pass
     * on the request and response to the next entity in the chain.</p>
     *
     * <p>The default implementation simply calls {@link FilterChain#doFilter}</p>
     *
     * @param req   a {@link HttpServletRequest} object that
     *              contains the request the client has made
     *              of the filter
     * @param res   a {@link HttpServletResponse} object that
     *              contains the response the filter sends
     *              to the client
     * @param chain the <code>FilterChain</code> for invoking the next filter or the resource
     * @throws IOException      if an input or output error is
     *                          detected when the filter handles
     *                          the request
     * @throws ServletException if the request for the could not be handled
     * @since Servlet 4.0
     */
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (req.getUserPrincipal() == null) {
            Cookie rmCookie = HttpUtils.findCookie(req, JREMEMBERMEID);
            if (rmCookie != null) {
                Optional<Account> accountByTokenHash = accountStore.getAccountByTokenHash(rmCookie.getValue());
                if (accountByTokenHash.isPresent()) {
                    req.login(accountByTokenHash.get().getLogin(), accountByTokenHash.get().getPassHash());
                }
            }
        }
        chain.doFilter(req, res);
    }
}
