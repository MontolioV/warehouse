package com.myapp.security.UT;

import com.myapp.communication.MailManager;
import com.myapp.security.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.mail.MessagingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.myapp.security.RestAccountActivator.MAIL_SUBJECT;
import static com.myapp.security.RestAccountActivator.QP_TOKEN;
import static com.myapp.security.TokenType.EMAIL_VERIFICATION;
import static com.myapp.utils.TestSecurityConstants.*;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * <p>Created by MontolioV on 17.07.18.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Response.class})
public class RestAccountActivatorTest {
    @InjectMocks
    private RestAccountActivator restAccountActivator;
    @Mock
    private AccountStore asMock;
    @Mock
    private TokenStore tsMock;
    @Mock
    private MailManager mmMock;
    @Mock
    private UriInfo uiMock;
    @Mock
    private Account accountMock;
    @Mock
    private Token tokenMock;
    @Mock
    private UriBuilder ubEmptyMock;
    @Mock
    private UriBuilder ubClassMock;
    @Mock
    private UriBuilder ubSetMock;
    @Mock
    private Response.ResponseBuilder rbMock;
    @Mock
    private Response responseMock;
    private URI uriActivation;
    private URI uriWebAppAddress;
    private String webAppAddress = "http://webAppAddress.com";

    @Before
    public void setUp() throws Exception {
        restAccountActivator.setWebAppAddress(webAppAddress);
        uriActivation = new URI("http://uriWithQParam.com");
        uriWebAppAddress = new URI("http://webAppAddress.com");

        when(tsMock.createToken(accountMock, EMAIL_VERIFICATION, 1, DAYS)).thenReturn(tokenMock);
        when(tokenMock.getTokenHash()).thenReturn(TOKEN_HASH_VALID);
        when(accountMock.getEmail()).thenReturn(EMAIL_VALID);
        when(accountMock.getLogin()).thenReturn(LOGIN_VALID);
        when(accountMock.getId()).thenReturn(1L);
        when(uiMock.getBaseUriBuilder()).thenReturn(ubEmptyMock);
        when(ubEmptyMock.path(RestAccountActivator.class)).thenReturn(ubClassMock);
        when(ubClassMock.queryParam(QP_TOKEN, TOKEN_HASH_VALID)).thenReturn(ubSetMock);
        when(ubSetMock.build(anyVararg())).thenReturn(uriActivation);
        when(asMock.getAccountByLogin(LOGIN_VALID)).thenReturn(Optional.of(accountMock));
        when(asMock.getAccountByLogin(LOGIN_INVALID)).thenReturn(Optional.empty());

        mockStatic(Response.class);
        when(Response.temporaryRedirect(eq(uriWebAppAddress))).thenReturn(rbMock);
        when(rbMock.build()).thenReturn(responseMock);
    }

    @Test
    public void prepareActivationSuccess() throws MessagingException, URISyntaxException {
        Response response = restAccountActivator.prepareActivation(LOGIN_VALID);
        verify(asMock).getAccountByLogin(LOGIN_VALID);
        verify(tsMock).createToken(accountMock, EMAIL_VERIFICATION, 1, DAYS);
        verify(mmMock).sendEmail(EMAIL_VALID, MAIL_SUBJECT, "<h1>Hi, " + LOGIN_VALID + "!</h1>" +
                "<p>Follow <a href='http://uriWithQParam.com'>link</a> to verify your account.</p>");
        assertThat(response, sameInstance(responseMock));
    }

    @Test
    public void prepareActivationFail() throws MessagingException, URISyntaxException {
        Response response = restAccountActivator.prepareActivation(LOGIN_INVALID);
        verify(asMock).getAccountByLogin(LOGIN_INVALID);
        verify(tsMock, never()).createToken(any(Account.class), any(TokenType.class), anyInt(), any(ChronoUnit.class));
        verify(mmMock, never()).sendEmail(anyString(), anyString(), anyString());
        assertThat(response, not(sameInstance(responseMock)));
    }

    @Test
    public void activate() throws URISyntaxException {
        Response response = restAccountActivator.activate(TOKEN_HASH_VALID);
        verify(asMock).activateAccount(TOKEN_HASH_VALID);
        verify(tsMock).removeToken(TOKEN_HASH_VALID);
        assertThat(response, sameInstance(responseMock));
    }
}