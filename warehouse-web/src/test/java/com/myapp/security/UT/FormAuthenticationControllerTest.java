package com.myapp.security.UT;

import com.myapp.security.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.security.enterprise.CallerPrincipal;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.myapp.utils.TestSecurityConstants.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 29.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class FormAuthenticationControllerTest {
    @InjectMocks
    private FormAuthenticationController controller;
    @Mock
    private CustomIdentityStore isMock;
    @Mock
    private CustomRememberMeIdentityStore rmMock;
    @Mock
    private AccountStore asMock;
    @Mock
    private Account accountMock;
    @Mock
    private FacesContext fcMock;
    @Mock
    private ExternalContext ecMock;
    @Mock
    private HttpServletRequest requestMock;
    private UsernamePasswordCredential credentialValid = new UsernamePasswordCredential(LOGIN_VALID, PASSWORD_VALID);
    private UsernamePasswordCredential credentialInvalid = new UsernamePasswordCredential(LOGIN_INVALID, PASSWORD_INVALID);
    private String cookieValue = "1234567890";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void submitSuccsess() throws ServletException {
        when(isMock.validate(any(UsernamePasswordCredential.class))).thenReturn(new CredentialValidationResult(LOGIN_VALID));
        when(asMock.getAccountByLogin(LOGIN_VALID)).thenReturn(Optional.of(accountMock));
        when(accountMock.getLogin()).thenReturn(LOGIN_VALID);
        when(accountMock.getPassHash()).thenReturn(PASS_HASH_VALID);
        when(fcMock.getExternalContext()).thenReturn(ecMock);
        when(ecMock.getRequest()).thenReturn(requestMock);
        when(rmMock.generateLoginToken(any(CallerPrincipal.class), any(Set.class))).thenReturn(cookieValue);

        controller.setLogin(LOGIN_VALID);
        controller.setPassword(PASSWORD_VALID);
        controller.setRememberMe(true);
        controller.submit();

        ArgumentCaptor<UsernamePasswordCredential> captorCredentials = ArgumentCaptor.forClass(UsernamePasswordCredential.class);
        verify(isMock).validate(captorCredentials.capture());
        assertThat(credentialValid.getCaller(), is(captorCredentials.getValue().getCaller()));
        assertThat(credentialValid.getPasswordAsString(), is(captorCredentials.getValue().getPasswordAsString()));

        verify(asMock).getAccountByLogin(eq(LOGIN_VALID));
        verify(rmMock).generateLoginToken(any(CallerPrincipal.class), any(Set.class));

        ArgumentCaptor<String> captorCookieName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> captorCookieValue = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map> captorCookieProperties = ArgumentCaptor.forClass(Map.class);
        verify(ecMock).addResponseCookie(captorCookieName.capture(), captorCookieValue.capture(), captorCookieProperties.capture());
        assertThat(captorCookieName.getValue(), is("JREMEMBERMEID"));
        assertThat(captorCookieValue.getValue(), is(cookieValue));
        assertThat(captorCookieProperties.getValue().get("maxAge"), is(60 * 60 * 24 * 14));

        verify(requestMock).logout();
        verify(requestMock).login(eq(LOGIN_VALID), eq(PASS_HASH_VALID));
    }

    @Test
    public void submitFail() throws ServletException {
        when(isMock.validate(any(UsernamePasswordCredential.class))).thenReturn(CredentialValidationResult.INVALID_RESULT);

        controller.setLogin(LOGIN_INVALID);
        controller.setPassword(PASSWORD_INVALID);
        String redirect = controller.submit();

        ArgumentCaptor<UsernamePasswordCredential> captor = ArgumentCaptor.forClass(UsernamePasswordCredential.class);

        verify(isMock).validate(captor.capture());
        assertThat(credentialInvalid.getCaller(), is(captor.getValue().getCaller()));
        assertThat(credentialInvalid.getPasswordAsString(), is(captor.getValue().getPasswordAsString()));

        verify(asMock, never()).getAccountByLogin(any(String.class));
        verify(requestMock, never()).logout();
        verify(requestMock, never()).login(any(String.class), any(String.class));
        assertThat(redirect,is("/login_error?faces-redirect=true"));
    }
}