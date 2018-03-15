package com.myapp.security.UT;

import com.myapp.security.Account;
import com.myapp.security.AccountStore;
import com.myapp.security.CustomIdentityStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.security.enterprise.credential.CallerOnlyCredential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import java.util.Optional;

import static javax.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 12.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomIdentityStoreTest implements SecurityConstants {
    @InjectMocks
    private CustomIdentityStore is;
    @Mock
    private AccountStore acMock;
    @Mock
    private UsernamePasswordCredential usernamePasswordCredentialMock;
    @Mock
    private CallerOnlyCredential callerOnlyCredentialMock;
    @Mock
    private Account accountMock;

    @Before
    public void setUp() throws Exception {
        when(callerOnlyCredentialMock.getCaller()).thenReturn(LOGIN_VALID);
        when(usernamePasswordCredentialMock.getCaller()).thenReturn(LOGIN_VALID);
        when(usernamePasswordCredentialMock.getPasswordAsString()).thenReturn(PASSWORD_VALID);
        when(acMock.getAccountByLogin(anyString())).thenReturn(Optional.of(accountMock));
        when(accountMock.getLogin()).thenReturn(LOGIN_VALID);
        when(accountMock.getRoles()).thenReturn(ROLES_LIST);
    }

    @Test
    public void validateUserPassValid() {
        when(acMock.getAccountByLoginAndPassword(anyString(), anyString())).thenReturn(Optional.of(accountMock));
        when(accountMock.isActive()).thenReturn(true);
        CredentialValidationResult result = is.validate(usernamePasswordCredentialMock);

        verify(acMock).getAccountByLoginAndPassword(LOGIN_VALID, PASSWORD_VALID);
        checkAuthenticationValidResult(result);
    }

    @Test
    public void validateUserPassInvalid() {
        when(acMock.getAccountByLoginAndPassword(anyString(), anyString())).thenReturn(Optional.empty());
        when(accountMock.isActive()).thenReturn(false);
        CredentialValidationResult result = is.validate(usernamePasswordCredentialMock);

        verify(acMock).getAccountByLoginAndPassword(LOGIN_VALID, PASSWORD_VALID);
        assertThat(result, is(INVALID_RESULT));
    }

    @Test
    public void validateUserOnlyActive() {
        when(accountMock.isActive()).thenReturn(true);
        CredentialValidationResult result = is.validate(callerOnlyCredentialMock);

        verify(acMock).getAccountByLogin(LOGIN_VALID);
        checkAuthenticationValidResult(result);
    }

    @Test
    public void validateUserOnlyInactive() {
        when(accountMock.isActive()).thenReturn(false);
        CredentialValidationResult result = is.validate(callerOnlyCredentialMock);

        verify(acMock).getAccountByLogin(LOGIN_VALID);
        assertThat(result, is(INVALID_RESULT));
    }
}