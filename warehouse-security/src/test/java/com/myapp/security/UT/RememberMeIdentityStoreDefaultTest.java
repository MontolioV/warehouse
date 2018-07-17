package com.myapp.security.UT;

import com.myapp.security.*;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.security.enterprise.CallerPrincipal;
import javax.security.enterprise.credential.RememberMeCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import java.util.Optional;

import static com.myapp.security.TokenType.REMEMBER_ME;
import static com.myapp.utils.TestSecurityConstants.*;
import static java.time.temporal.ChronoUnit.DAYS;
import static javax.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 13.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class RememberMeIdentityStoreDefaultTest implements CommonChecks {
    @InjectMocks
    private RememberMeIdentityStoreDefault rememberMeIS;
    @Mock
    private AccountStore asMock;
    @Mock
    private TokenStore tsMock;
    @Mock
    private Token tokenMock;
    @Mock
    private CallerPrincipal callerPrincipalValid;
    @Mock
    private CallerPrincipal callerPrincipalInvalid;
    @Mock
    private RememberMeCredential rememberMeCredValidMock;
    @Mock
    private RememberMeCredential rememberMeCredInvalidMock;
    @Mock
    private Account accountMock;

    @Before
    public void setUp() throws Exception {
        when(callerPrincipalValid.getName()).thenReturn(LOGIN_VALID);
        when(callerPrincipalInvalid.getName()).thenReturn(LOGIN_INVALID);
        when(asMock.getAccountByLogin(LOGIN_VALID)).thenReturn(Optional.of(accountMock));
        when(asMock.getAccountByLogin(LOGIN_INVALID)).thenReturn(Optional.empty());
        when(asMock.getAccountByTokenHash(TOKEN_HASH_VALID)).thenReturn(Optional.of(accountMock));
        when(asMock.getAccountByTokenHash(TOKEN_HASH_INVALID)).thenReturn(Optional.empty());
        when(tsMock.createToken(accountMock, REMEMBER_ME, 14, DAYS)).thenReturn(tokenMock);
        when(tokenMock.getTokenHash()).thenReturn(TOKEN_HASH_VALID);
        when(rememberMeCredValidMock.getToken()).thenReturn(TOKEN_HASH_VALID);
        when(rememberMeCredInvalidMock.getToken()).thenReturn(TOKEN_HASH_INVALID);
        when(accountMock.getLogin()).thenReturn(LOGIN_VALID);
        when(accountMock.getRoles()).thenReturn(ROLES_SET);
    }

    @Test
    public void validate() {
        CredentialValidationResult resultValid = rememberMeIS.validate(rememberMeCredValidMock);
        CredentialValidationResult resultInvalid = rememberMeIS.validate(rememberMeCredInvalidMock);

        assertThat(resultInvalid, is(INVALID_RESULT));
        checkAuthenticationValidResult(resultValid);
    }

    @Test
    public void generateLoginToken() {
        String resultValid = rememberMeIS.generateLoginToken(callerPrincipalValid, null);
        String resultInvalid = rememberMeIS.generateLoginToken(callerPrincipalInvalid, null);

        assertThat(resultValid, CoreMatchers.is(TOKEN_HASH_VALID));
        assertNull(resultInvalid);
        verify(tsMock).createToken(accountMock, REMEMBER_ME, 14, DAYS);
    }

    @Test
    public void removeLoginToken() {
        rememberMeIS.removeLoginToken(TOKEN_HASH_VALID);
        verify(tsMock).removeToken(TOKEN_HASH_VALID);
    }
}