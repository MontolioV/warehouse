package com.myapp.security.UT;

import com.myapp.security.LogoutServlet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.security.enterprise.identitystore.RememberMeIdentityStore;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.myapp.utils.CookiesConstants.JREMEMBERMEID;
import static com.myapp.utils.CookiesConstants.MAX_AGE_TO_REMOVE_INSTANTLY;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 04.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class LogoutServletTest {
    @InjectMocks
    private LogoutServlet logoutServlet = new LogoutServlet();
    @Mock
    private RememberMeIdentityStore rmStoreMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private HttpServletResponse responseMock;
    private String indexPath = "path";
    private String cookieHash = "hash";

    @Test
    public void doGet() throws ServletException, IOException {
        when(requestMock.getMethod()).thenReturn("GET");
        when(requestMock.getCookies()).thenReturn(new Cookie[]{
                new Cookie("1", "1"),
                new Cookie(JREMEMBERMEID, cookieHash)
        });
        when(requestMock.getContextPath()).thenReturn(indexPath);

        logoutServlet.service(requestMock, responseMock);

        ArgumentCaptor<Cookie> cookieArgumentCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(responseMock).addCookie(cookieArgumentCaptor.capture());
        Cookie newCookie = cookieArgumentCaptor.getValue();
        assertThat(newCookie.getName(), is(JREMEMBERMEID));
        assertThat(newCookie.getMaxAge(), is(MAX_AGE_TO_REMOVE_INSTANTLY));

        verify(rmStoreMock).removeLoginToken(cookieHash);
        verify(requestMock).logout();
        verify(requestMock).getContextPath();
        verify(responseMock).sendRedirect(indexPath);
    }
}