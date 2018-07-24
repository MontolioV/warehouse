package com.myapp.utils;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Created by MontolioV on 04.04.18.
 */
@ApplicationScoped
public class HttpUtils {

    public Cookie findCookie(HttpServletRequest httpServletRequest, String cookieName) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie;
                }
            }
        }
        return null;
    }
}
