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
import java.util.Optional;

import static com.myapp.security.RestAccountActivator.MAIL_SUBJECT;
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
    private Account accountMock;
    @Mock
    private Token tokenMock;

    @Before
    public void setUp() throws Exception {
        when(tsMock.createToken(accountMock, EMAIL_VERIFICATION, 1, DAYS)).thenReturn(tokenMock);
        when(tokenMock.getTokenHash()).thenReturn(TOKEN_HASH_VALID);
        when(accountMock.getEmail()).thenReturn(EMAIL);
        when(accountMock.getLogin()).thenReturn(LOGIN_VALID);
        when(accountMock.getId()).thenReturn(1L);
        when(asMock.getAccountByTokenHash(TOKEN_HASH_VALID)).thenReturn(Optional.of(accountMock));
        when(asMock.getAccountByTokenHash(TOKEN_HASH_INVALID)).thenReturn(Optional.empty());
    }

    @Test
    public void prepareActivationSuccess() throws MessagingException {
        restAccountActivator.prepareActivation(accountMock);
        verify(tsMock).createToken(accountMock, EMAIL_VERIFICATION, 1, DAYS);
        verify(mmMock).sendEmail(EMAIL, MAIL_SUBJECT, "<h1>Hi, " + LOGIN_VALID + "!</h1>" +
                "<p>Follow <a href='http://localhost:8080/warehouse/activation?token=" +
                TOKEN_HASH_VALID + "'>link</a> to verify your account:</p>");
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