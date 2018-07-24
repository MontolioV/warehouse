package com.myapp.security.UT;

import com.myapp.security.LogoutServlet;
import com.myapp.utils.HttpUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.security.enterprise.identitystore.RememberMeIdentityStore;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.myapp.utils.CookiesConstants.JREMEMBERMEID;
import static com.myapp.utils.CookiesConstants.MAX_AGE_TO_REMOVE_INSTANTLY;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 04.04.18.
 */
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor({
        "javax.servlet.http.Cookie",
        "javax.servlet.http.HttpServlet",
        "javax.servlet.GenericServlet"
})
public class LogoutServletTest {
    @InjectMocks
    private LogoutServlet logoutServlet;
    @Mock
    private RememberMeIdentityStore rmStoreMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private HttpServletResponse responseMock;
    @Mock
    private HttpUtils httpUtilsMock;
    @Mock
    private Cookie cookieTargetMock;
    private String indexPath = "path";
    private String cookieHash = "hash";

    @Test
    public void doGet() throws ServletException, IOException {
        when(httpUtilsMock.findCookie(requestMock, JREMEMBERMEID)).thenReturn(cookieTargetMock);
        when(cookieTargetMock.getName()).thenReturn(JREMEMBERMEID);
        when(cookieTargetMock.getValue()).thenReturn(cookieHash);

        when(requestMock.getMethod()).thenReturn("GET");
        when(requestMock.getContextPath()).thenReturn(indexPath);

        logoutServlet.service(requestMock, responseMock);

        verify(rmStoreMock).removeLoginToken(cookieHash);
        verify(cookieTargetMock).setMaxAge(MAX_AGE_TO_REMOVE_INSTANTLY);
        verify(responseMock).addCookie(cookieTargetMock);
        verify(requestMock).logout();
        verify(requestMock).getContextPath();
        verify(responseMock).sendRedirect(indexPath);
    }
}