package com.myapp.security.UT;

import com.myapp.security.Account;
import com.myapp.security.AccountStore;
import com.myapp.security.CustomIdentityStore;
import com.myapp.security.Roles;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.security.enterprise.credential.CallerOnlyCredential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import java.util.*;

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
public class CustomIdentityStoreTest {
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
    private String login = "login";
    private String pass = "pass";
    private List<Roles> rolesList = new ArrayList<>();
    private Set<String> rolesSet = new HashSet<>();

    @Before
    public void setUp() throws Exception {
        when(callerOnlyCredentialMock.getCaller()).thenReturn(login);
        when(usernamePasswordCredentialMock.getCaller()).thenReturn(login);
        when(usernamePasswordCredentialMock.getPasswordAsString()).thenReturn(pass);
        when(acMock.getAccountByLogin(anyString())).thenReturn(Optional.of(accountMock));
        when(accountMock.getLogin()).thenReturn(login);
        when(accountMock.getRoles()).thenReturn(rolesList);
        rolesList.add(Roles.ADMIN);
        rolesSet.add(Roles.ADMIN.name());
    }

    @Test
    public void validateUserPassValid() {
        when(acMock.getAccountByLoginAndPassword(anyString(), anyString())).thenReturn(Optional.of(accountMock));
        when(accountMock.isActive()).thenReturn(true);
        CredentialValidationResult result = is.validate(usernamePasswordCredentialMock);

        verify(acMock).getAccountByLoginAndPassword(login, pass);
        checkValidResult(result);
    }

    @Test
    public void validateUserPassInvalid() {
        when(acMock.getAccountByLoginAndPassword(anyString(), anyString())).thenReturn(Optional.empty());
        when(accountMock.isActive()).thenReturn(false);
        CredentialValidationResult result = is.validate(usernamePasswordCredentialMock);

        verify(acMock).getAccountByLoginAndPassword(login, pass);
        assertThat(result, is(INVALID_RESULT));
    }

    @Test
    public void validateUserOnlyActive() {
        when(accountMock.isActive()).thenReturn(true);
        CredentialValidationResult result = is.validate(callerOnlyCredentialMock);

        verify(acMock).getAccountByLogin(login);
        checkValidResult(result);
    }

    @Test
    public void validateUserOnlyInactive() {
        when(accountMock.isActive()).thenReturn(false);
        CredentialValidationResult result = is.validate(callerOnlyCredentialMock);

        verify(acMock).getAccountByLogin(login);
        assertThat(result, is(INVALID_RESULT));
    }

    private void checkValidResult(CredentialValidationResult result) {
        assertThat(result.getCallerPrincipal().getName(), is(login));
        assertThat(result.getCallerGroups(), is(rolesSet));
    }
}