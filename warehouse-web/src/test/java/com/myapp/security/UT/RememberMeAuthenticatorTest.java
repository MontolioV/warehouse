package com.myapp.security.UT;

import com.myapp.security.Account;
import com.myapp.security.AccountStore;
import com.myapp.security.RememberMeAuthenticator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.security.enterprise.CallerPrincipal;
import javax.security.enterprise.credential.RememberMeCredential;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

import static com.myapp.utils.CookiesConstants.JREMEMBERMEID;
import static com.myapp.utils.TestSecurityConstants.LOGIN_VALID;
import static com.myapp.utils.TestSecurityConstants.PASS_HASH_VALID;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 05.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class RememberMeAuthenticatorTest {
    @InjectMocks
    private RememberMeAuthenticator authenticator;
    @Mock
    private AccountStore asMock;
    @Mock
    private HttpServletRequest requestMock;

    private Cookie[] cookies = {new Cookie("1", "1"),
            new Cookie("2", "2")};
    private String cookieHash = "hash";
    private RememberMeCredential credential = new RememberMeCredential(cookieHash);

    @Before
    public void setUp() throws Exception {
        when(requestMock.getUserPrincipal()).thenReturn(null);
        when(requestMock.getCookies()).thenReturn(cookies);
    }

    @Test
    public void failAlreadyLoggedIn() throws IOException, ServletException {
        when(requestMock.getUserPrincipal()).thenReturn(new CallerPrincipal(LOGIN_VALID));

        authenticator.cookieAuth(requestMock);
        verify(requestMock, never()).getCookies();
        verify(asMock, never()).getAccountByTokenHash(anyString());
        verify(requestMock, never()).login(anyString(), anyString());
    }

    @Test
    public void failNoCookie() throws IOException, ServletException {
        authenticator.cookieAuth(requestMock);

        verify(requestMock).getCookies();
        verify(asMock, never()).getAccountByTokenHash(anyString());
        verify(requestMock, never()).login(anyString(), anyString());
    }

    @Test
    public void success() throws IOException, ServletException {
        cookies[1] = new Cookie(JREMEMBERMEID, cookieHash);
        when(asMock.getAccountByTokenHash(cookieHash)).thenReturn(Optional.of(
                new Account(0, LOGIN_VALID, PASS_HASH_VALID, "", null, null)));

        authenticator.cookieAuth(requestMock);

        verify(requestMock).getCookies();
        verify(asMock).getAccountByTokenHash(cookieHash);
        verify(requestMock).login(LOGIN_VALID, PASS_HASH_VALID);
    }
}