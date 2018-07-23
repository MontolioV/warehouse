package com.myapp.security.UT;

import com.myapp.communication.MailManager;
import com.myapp.security.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.mail.MessagingException;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;

import static com.myapp.security.RestAccountActivator.MAIL_SUBJECT;
import static com.myapp.security.RestAccountActivator.QP_TOKEN;
import static com.myapp.security.TokenType.EMAIL_VERIFICATION;
import static com.myapp.utils.TestSecurityConstants.*;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 17.07.18.
 */
@RunWith(MockitoJUnitRunner.class)
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
    private UriBuilder ubSetMock;
    private URI uriMock;

    @Before
    public void setUp() throws Exception {
        uriMock = new URI("http://uriWithQParam.com");

        when(tsMock.createToken(accountMock, EMAIL_VERIFICATION, 1, DAYS)).thenReturn(tokenMock);
        when(tokenMock.getTokenHash()).thenReturn(TOKEN_HASH_VALID);
        when(accountMock.getEmail()).thenReturn(EMAIL_VALID);
        when(accountMock.getLogin()).thenReturn(LOGIN_VALID);
        when(accountMock.getId()).thenReturn(1L);
        when(asMock.getAccountByTokenHash(TOKEN_HASH_VALID)).thenReturn(Optional.of(accountMock));
        when(asMock.getAccountByTokenHash(TOKEN_HASH_INVALID)).thenReturn(Optional.empty());
        when(uiMock.getAbsolutePathBuilder()).thenReturn(ubEmptyMock);
        when(ubEmptyMock.queryParam(QP_TOKEN, TOKEN_HASH_VALID)).thenReturn(ubSetMock);
        when(ubSetMock.build(anyVararg())).thenReturn(uriMock);
    }

    @Test
    public void prepareActivationSuccess() throws MessagingException {
        restAccountActivator.prepareActivation(accountMock);
        verify(tsMock).createToken(accountMock, EMAIL_VERIFICATION, 1, DAYS);
        verify(mmMock).sendEmail(EMAIL_VALID, MAIL_SUBJECT, "<h1>Hi, " + LOGIN_VALID + "!</h1>" +
//                "<p>Follow <a href='http://uriWithQParam.com'>link</a> to verify your account:</p>");
                "<p>Follow <a href='http://37.229.148.120/warehouse/rs/activation?token=TOKEN_HASH_VALID'>link</a> to verify your account:</p>");
    }

    @Test
    public void activateRightHash() {
        restAccountActivator.activate(TOKEN_HASH_VALID);
        verify(asMock).changeAccountStatus(1L, true);
        verify(tsMock).removeToken(TOKEN_HASH_VALID);
    }

    @Test
    public void activateWrongHash() {
        restAccountActivator.activate(TOKEN_HASH_INVALID);
        verify(asMock, never()).changeAccountStatus(anyLong(), eq(true));
        verify(tsMock, never()).removeToken(TOKEN_HASH_INVALID);
    }
}