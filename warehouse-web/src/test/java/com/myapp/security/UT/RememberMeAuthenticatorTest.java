package com.myapp.security.UT;

import com.myapp.security.Account;
import com.myapp.security.AccountStore;
import com.myapp.security.RememberMeAuthenticator;
import com.myapp.utils.HttpUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.security.enterprise.CallerPrincipal;
import javax.security.enterprise.credential.RememberMeCredential;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.myapp.utils.CookiesConstants.JREMEMBERMEID;
import static com.myapp.utils.TestSecurityConstants.LOGIN_VALID;
import static com.myapp.utils.TestSecurityConstants.PASS_HASH_VALID;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 05.04.18.
 */
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("javax.servlet.http.Cookie")
public class RememberMeAuthenticatorTest {
    @InjectMocks
    private RememberMeAuthenticator authenticator;
    @Mock
    private AccountStore asMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private HttpUtils huMock;
    @Mock
    private Cookie cookieMock;
    private String cookieHash = "hash";
    private RememberMeCredential credential = new RememberMeCredential(cookieHash);

    @Before
    public void setUp() {
        when(huMock.findCookie(requestMock, JREMEMBERMEID)).thenReturn(cookieMock);

        when(cookieMock.getName()).thenReturn(JREMEMBERMEID);
        when(cookieMock.getValue()).thenReturn(cookieHash);
        when(requestMock.getUserPrincipal()).thenReturn(null);
    }

    @Test
    public void failAlreadyLoggedIn() throws ServletException {
        when(requestMock.getUserPrincipal()).thenReturn(new CallerPrincipal(LOGIN_VALID));

        authenticator.cookieAuth(requestMock);
        verify(huMock, never()).findCookie(eq(requestMock), anyString());
        verify(asMock, never()).getAccountByTokenHash(anyString());
        verify(requestMock, never()).login(anyString(), anyString());
    }

    @Test
    public void failNoCookie() throws ServletException {
        when(huMock.findCookie(requestMock, JREMEMBERMEID)).thenReturn(null);
        authenticator.cookieAuth(requestMock);

        verify(huMock).findCookie(requestMock, JREMEMBERMEID);
        verify(asMock, never()).getAccountByTokenHash(anyString());
        verify(requestMock, never()).login(anyString(), anyString());
    }

    @Test
    public void success() throws ServletException {
        when(asMock.getAccountByTokenHash(cookieHash)).thenReturn(Optional.of(
                new Account(0, LOGIN_VALID, PASS_HASH_VALID, "", null, null)));

        authenticator.cookieAuth(requestMock);

        verify(huMock).findCookie(requestMock, JREMEMBERMEID);
        verify(asMock).getAccountByTokenHash(cookieHash);
        verify(requestMock).login(LOGIN_VALID, PASS_HASH_VALID);
    }
}