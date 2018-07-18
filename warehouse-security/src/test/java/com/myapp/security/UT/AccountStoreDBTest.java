package com.myapp.security.UT;

import com.myapp.security.*;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ejb.SessionContext;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.myapp.security.Account.LOGIN_PARAM;
import static com.myapp.security.Roles.*;
import static com.myapp.security.Token.HASH_PARAM;
import static com.myapp.utils.TestSecurityConstants.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 05.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountStoreDBTest implements CommonChecks {
    @InjectMocks
    private AccountStoreDB accountStoreDB;
    @Mock
    private Encryptor encryptorMock;
    @Mock
    private EntityManager emMock;
    @Mock
    private AccountActivator aaMock;
    @Mock
    private SessionContext contextMock;
    @Mock
    private Principal principalMock;
    @Mock
    private Account accountMock;
    private Account accountNew;
    private Account accountExisting;
    private long accountExistingID = 0L;
    private long accountNewID = 1L;
    private long accountMockID = 2L;
    private long accountNonExistingID = 3L;
    private String[] badPasswords = {"Shrt12", "NoNumerals", "nocapitals238",};

    @Before
    public void setUp() throws Exception {
        accountNew = new Account(accountNewID, "new_login", PASSWORD_VALID, "new_email", new ArrayList<>(), new HashSet<>());
        accountExisting = new Account(accountExistingID, "existing_login", PASS_HASH_VALID, "existing_email", new ArrayList<>(), new HashSet<>());

        when(contextMock.getCallerPrincipal()).thenReturn(principalMock);
        when(principalMock.getName()).thenReturn(LOGIN_VALID);

        when(encryptorMock.generate(PASSWORD_VALID)).thenReturn(PASS_HASH_VALID);
        when(encryptorMock.generate(PASSWORD_INVALID)).thenReturn(PASS_HASH_INVALID);
        for (String badPassword : badPasswords) {
            when(encryptorMock.generate(badPassword)).thenReturn(PASS_HASH_INVALID);
        }
        when(encryptorMock.verify(PASSWORD_VALID, PASS_HASH_VALID)).thenReturn(true);
        when(encryptorMock.verify(PASSWORD_INVALID, PASS_HASH_VALID)).thenReturn(false);

        when(emMock.merge(accountNew)).thenReturn(accountNew);
        when(emMock.merge(accountExisting)).thenReturn(accountExisting);
        when(emMock.merge(accountMock)).thenReturn(accountMock);

        TypedQuery<Account> getByLoginQueryMock = mock(TypedQuery.class);
        when(emMock.createNamedQuery(Account.GET_BY_LOGIN, Account.class)).thenReturn(getByLoginQueryMock);
        TypedQuery<Account> getByTokenHashMock = mock(TypedQuery.class);
        when(emMock.createNamedQuery(Account.GET_BY_TOKEN_HASH, Account.class)).thenReturn(getByTokenHashMock);

        ArrayList<Account> accountsEmpty = new ArrayList<>();
        TypedQuery<Account> uniqueLoginQueryMock = mock(TypedQuery.class);
        when(getByLoginQueryMock.setParameter(LOGIN_PARAM, accountNew.getLogin())).thenReturn(uniqueLoginQueryMock);
        when(getByTokenHashMock.setParameter(HASH_PARAM, TOKEN_HASH_INVALID)).thenReturn(uniqueLoginQueryMock);
        when(uniqueLoginQueryMock.getResultList()).thenReturn(accountsEmpty);
        when(uniqueLoginQueryMock.getSingleResult()).thenThrow(new NoResultException());

        ArrayList<Account> accountsNotEmpty = new ArrayList<>();
        accountsNotEmpty.add(accountExisting);
        TypedQuery<Account> existingLoginQueryMock = mock(TypedQuery.class);
        when(getByLoginQueryMock.setParameter(LOGIN_PARAM, accountExisting.getLogin())).thenReturn(existingLoginQueryMock);
        when(getByTokenHashMock.setParameter(HASH_PARAM, TOKEN_HASH_VALID)).thenReturn(existingLoginQueryMock);
        when(existingLoginQueryMock.getResultList()).thenReturn(accountsNotEmpty);
        when(existingLoginQueryMock.getSingleResult()).thenReturn(accountExisting);

        TypedQuery<Account> mockAccountLoginQueryMock = mock(TypedQuery.class);
        when(getByLoginQueryMock.setParameter(LOGIN_PARAM, LOGIN_VALID)).thenReturn(mockAccountLoginQueryMock);
        when(mockAccountLoginQueryMock.getSingleResult()).thenReturn(accountMock);

        when(emMock.find(Account.class, accountExistingID)).thenReturn(accountExisting);
        when(emMock.find(Account.class, accountNewID)).thenReturn(accountNew);
        when(emMock.find(Account.class, accountMockID)).thenReturn(accountMock);
        when(emMock.find(Account.class, accountNonExistingID)).thenReturn(null);
    }

    @Test
    public void createAccount() throws LoginExistsException, UnsecurePasswordException, MessagingException {
        accountStoreDB.createAccount(accountNew);
        verify(emMock).persist(accountNew);
        verify(emMock).flush();
        verify(emMock).detach(accountNew);
        verify(aaMock).prepareActivation(accountNew);

        assertThat(accountNew.getId(), is(1L));
        assertThat(accountNew.getLogin(), is("new_login"));
        MatcherAssert.assertThat(accountNew.getPassHash(), CoreMatchers.is(PASS_HASH_VALID));
        assertThat(accountNew.getEmail(), is("new_email"));
        assertThat(accountNew.isActive(), is(false));
        assertThat(accountNew.getRoles().size(), is(1));
        assertTrue(accountNew.getRoles().contains(USER));
        assertTrue(accountNew.getTokens().isEmpty());
    }

    @Test
    public void createExistingAccount() throws UnsecurePasswordException, MessagingException {
        try {
            accountStoreDB.createAccount(accountExisting);
        } catch (LoginExistsException e) {
            //as expected
        }
        try {
            accountStoreDB.createAccount(accountExisting);
        } catch (LoginExistsException e) {
            //as expected
        }
        verify(emMock, never()).persist(any(Account.class));
        verify(emMock, never()).detach(any(Account.class));
        verify(aaMock, never()).prepareActivation(any(Account.class));
    }

    @Test
    public void createAccountWithBadPass() throws LoginExistsException, MessagingException {
        for (String password : badPasswords) {
            accountNew.setPassHash(password);
            try {
                accountStoreDB.createAccount(accountNew);
            } catch (UnsecurePasswordException e) {
                //as expected
            }
        }
        verify(emMock, never()).persist(any(Account.class));
        verify(emMock, never()).detach(any(Account.class));
        verify(aaMock, never()).prepareActivation(any(Account.class));
    }

    @Test
    public void getAccountByLogin() {
        Optional<Account> accountNotExists = accountStoreDB.getAccountByLogin(accountNew.getLogin());
        Optional<Account> accountExists = accountStoreDB.getAccountByLogin(accountExisting.getLogin());

        assertFalse(accountNotExists.isPresent());
        assertTrue(accountExists.isPresent());
        assertThat(accountExists.get(), is(accountExisting));
        verify(emMock, times(2)).createNamedQuery(Account.GET_BY_LOGIN, Account.class);
        verify(emMock).detach(accountExisting);
    }

    @Test
    public void getAccountByLoginAndPassword() {
        Optional<Account> accountPassRight = accountStoreDB.getAccountByLoginAndPassword(accountExisting.getLogin(), PASSWORD_VALID);
        Optional<Account> accountPassWrong = accountStoreDB.getAccountByLoginAndPassword(accountExisting.getLogin(), PASSWORD_INVALID);

        verify(encryptorMock, times(2)).verify(any(String.class), any(String.class));
        assertTrue(accountPassRight.isPresent());
        assertFalse(accountPassWrong.isPresent());
        assertThat(accountPassRight.get(), is(accountExisting));
        verify(emMock, times(2)).detach(accountExisting);
    }

    @Test
    public void getAccountByTokenHash() {
        Optional<Account> accountTokenValid = accountStoreDB.getAccountByTokenHash(TOKEN_HASH_VALID);
        Optional<Account> accountTokenInvalid = accountStoreDB.getAccountByTokenHash(TOKEN_HASH_INVALID);

        assertTrue(accountTokenValid.isPresent());
        assertFalse(accountTokenInvalid.isPresent());
        verify(emMock).detach(accountExisting);
    }

    @Test
    public void getAllAccounts() {
        TypedQuery<Account> customQueryMock = mock(TypedQuery.class);
        ArrayList<Account> arrayList = new ArrayList<>();
        arrayList.add(accountExisting);
        when(emMock.createNamedQuery(Account.GET_ALL, Account.class)).thenReturn(customQueryMock);
        when(customQueryMock.getResultList()).thenReturn(arrayList);
        assertThat(accountStoreDB.getAllAccounts(), is(arrayList));
        verify(emMock).detach(accountExisting);
    }

    @Test
    public void changeExistingAccountStatus() {
        accountStoreDB.changeAccountStatus(accountMockID, true);
        verify(emMock).find(Account.class, accountMockID);
        verify(accountMock).setActive(true);
    }

    @Test(expected = NoResultException.class)
    public void changeNonExistingAccountStatus() {
        accountStoreDB.changeAccountStatus(accountNonExistingID, true);
    }

    @Test
    public void changeExistingAccountPassword() throws UnsecurePasswordException {
        accountStoreDB.changeAccountPassword(accountMockID, PASSWORD_VALID);
        verify(encryptorMock).generate(PASSWORD_VALID);
        verify(emMock).find(Account.class, accountMockID);
        verify(accountMock).setPassHash(PASS_HASH_VALID);
    }

    @Test(expected = NoResultException.class)
    public void changeNonExistingAccountPassword() throws UnsecurePasswordException {
        accountStoreDB.changeAccountPassword(accountNonExistingID, PASSWORD_VALID);
    }

    @Test
    public void changeSelfAccountPassword() throws UnsecurePasswordException {
        accountStoreDB.changeSelfAccountPassword(PASSWORD_VALID);
        verify(encryptorMock).generate(PASSWORD_VALID);
        verify(accountMock).setPassHash(PASS_HASH_VALID);
    }

    @Test
    public void changeAccountPasswordFail() {
        for (String password : badPasswords) {
            try {
                accountStoreDB.changeAccountPassword(accountMockID, password);
            } catch (UnsecurePasswordException e) {
                //as expected
            }
            try {
                accountStoreDB.changeSelfAccountPassword(password);
            } catch (UnsecurePasswordException e) {
                //as expected
            }
            verify(encryptorMock, never()).generate(password);
            verify(accountMock, never()).setPassHash(PASS_HASH_INVALID);
            verify(emMock, never()).find(Account.class, accountMockID);
        }
    }

    @Test
    public void changeExistingAccountEmail() {
        accountStoreDB.changeAccountEmail(accountMockID, "email");
        verify(emMock).find(Account.class, accountMockID);
        verify(accountMock).setEmail("email");
    }

    @Test(expected = NoResultException.class)
    public void changeNonExistingAccountEmail() {
        accountStoreDB.changeAccountEmail(accountNonExistingID, "email");
    }

    @Test
    public void changeSelfAccountEmail() {
        accountStoreDB.changeSelfAccountEmail("email");
        verify(accountMock).setEmail("email");
    }

    @Test
    public void addRoleToExistingAccount() {
        assertTrue(accountExisting.getRoles().isEmpty());

        accountStoreDB.addRoleToAccount(accountExistingID, MODERATOR);
        assertThat(accountExisting.getRoles().size(), is(1));
        assertTrue(accountExisting.getRoles().contains(MODERATOR));

        accountStoreDB.addRoleToAccount(accountExistingID, MODERATOR);
        assertThat(accountExisting.getRoles().size(), is(1));
        assertTrue(accountExisting.getRoles().contains(MODERATOR));

        verify(emMock, times(2)).find(Account.class, accountExistingID);
    }

    @Test(expected = NoResultException.class)
    public void addRoleToNonExistingAccount() {
        accountStoreDB.addRoleToAccount(accountNonExistingID, MODERATOR);
    }

    @Test
    public void removeRoleFromExistingAccount() {
        assertTrue(accountExisting.getRoles().isEmpty());
        accountExisting.getRoles().add(ADMIN);
        accountExisting.getRoles().add(MODERATOR);

        accountStoreDB.removeRoleFromAccount(accountExistingID, ADMIN);

        assertThat(accountExisting.getRoles().size(), is(1));
        assertTrue(accountExisting.getRoles().contains(MODERATOR));
        verify(emMock).find(Account.class, accountExistingID);
    }

    @Test(expected = NoResultException.class)
    public void removeRoleFromNonExistingAccount() {
        accountStoreDB.removeRoleFromAccount(accountNonExistingID, MODERATOR);
    }

    @Test
    public void setNewRolesToExistingAccount() {
        Set<Roles> roles = new HashSet<>();
        accountStoreDB.setNewRolesToAccount(accountExistingID, roles);
        verify(emMock).find(Account.class, accountExistingID);
        assertThat(accountExisting.getRoles(), sameInstance(roles));
    }

    @Test(expected = NoResultException.class)
    public void setNewRolesToNonExistingAccount() {
        Set<Roles> roles = new HashSet<>();
        accountStoreDB.setNewRolesToAccount(accountNonExistingID, roles);
    }
}