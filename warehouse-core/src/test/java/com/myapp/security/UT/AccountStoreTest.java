package com.myapp.security.UT;

import com.myapp.security.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.myapp.security.Roles.*;
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
public class AccountStoreTest implements CommonChecks {
    @InjectMocks
    private AccountStore accountStore;
    @Mock
    private Encryptor encryptorMock;
    @Mock
    private EntityManager emMock;
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
        accountNew = new Account(accountNewID, "test", PASSWORD_VALID, "test", new ArrayList<>(), new HashSet<>());
        accountExisting = new Account(accountExistingID, "existing", PASS_HASH_VALID, "existing", new ArrayList<>(), new HashSet<>());

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
        when(getByLoginQueryMock.setParameter("login", accountNew.getLogin())).thenReturn(uniqueLoginQueryMock);
        when(getByTokenHashMock.setParameter("hash", TOKEN_HASH_INVALID)).thenReturn(uniqueLoginQueryMock);
        when(uniqueLoginQueryMock.getResultList()).thenReturn(accountsEmpty);
        when(uniqueLoginQueryMock.getSingleResult()).thenThrow(new NoResultException());

        ArrayList<Account> accountsNotEmpty = new ArrayList<>();
        accountsNotEmpty.add(accountExisting);
        TypedQuery<Account> existingLoginQueryMock = mock(TypedQuery.class);
        when(getByLoginQueryMock.setParameter("login", accountExisting.getLogin())).thenReturn(existingLoginQueryMock);
        when(getByTokenHashMock.setParameter("hash", TOKEN_HASH_VALID)).thenReturn(existingLoginQueryMock);
        when(existingLoginQueryMock.getResultList()).thenReturn(accountsNotEmpty);
        when(existingLoginQueryMock.getSingleResult()).thenReturn(accountExisting);

        TypedQuery<Account> mockAccountLoginQueryMock = mock(TypedQuery.class);
        when(getByLoginQueryMock.setParameter("login", LOGIN_VALID)).thenReturn(mockAccountLoginQueryMock);
        when(mockAccountLoginQueryMock.getSingleResult()).thenReturn(accountMock);

        when(emMock.find(Account.class, accountExistingID)).thenReturn(accountExisting);
        when(emMock.find(Account.class, accountNewID)).thenReturn(accountNew);
        when(emMock.find(Account.class, accountMockID)).thenReturn(accountMock);
        when(emMock.find(Account.class, accountNonExistingID)).thenReturn(null);
    }

    @Test
    public void createAccount() throws LoginExistsException, UnsecurePasswordException {
        accountStore.createAccount(accountNew);
        verify(emMock).persist(accountNew);
        verify(emMock).flush();
        verify(emMock).detach(accountNew);

        assertThat(accountNew.getId(), is(1L));
        assertThat(accountNew.getLogin(), is("test"));
        assertThat(accountNew.getPassHash(), is(PASS_HASH_VALID));
        assertThat(accountNew.getEmail(), is("test"));
        assertThat(accountNew.isActive(), is(false));
        assertThat(accountNew.getRoles().size(), is(1));
        assertTrue(accountNew.getRoles().contains(USER));
        assertTrue(accountNew.getTokens().isEmpty());
    }

    @Test
    public void createExistingAccount() throws UnsecurePasswordException {
        try {
            accountStore.createAccount(accountExisting);
        } catch (LoginExistsException e) {
            //as expected
        }
        verify(emMock, never()).persist(accountNew);
    }

    @Test
    public void createAccountWithBadPass() throws LoginExistsException {
        for (String password : badPasswords) {
            accountNew.setPassHash(password);
            try {
                accountStore.createAccount(accountNew);
            } catch (UnsecurePasswordException e) {
                //as expected
            }
        }
        verify(emMock, never()).persist(accountNew);
        verify(emMock, never()).detach(accountNew);
    }

    @Test
    public void getAccountByLogin() {
        Optional<Account> accountNotExists = accountStore.getAccountByLogin(accountNew.getLogin());
        Optional<Account> accountExists = accountStore.getAccountByLogin(accountExisting.getLogin());

        assertFalse(accountNotExists.isPresent());
        assertTrue(accountExists.isPresent());
        assertThat(accountExists.get(), is(accountExisting));
        verify(emMock, times(2)).createNamedQuery(Account.GET_BY_LOGIN, Account.class);
        verify(emMock).detach(accountExisting);
    }

    @Test
    public void getAccountByLoginAndPassword() {
        Optional<Account> accountPassRight = accountStore.getAccountByLoginAndPassword(accountExisting.getLogin(), PASSWORD_VALID);
        Optional<Account> accountPassWrong = accountStore.getAccountByLoginAndPassword(accountExisting.getLogin(), PASSWORD_INVALID);

        verify(encryptorMock, times(2)).verify(any(String.class), any(String.class));
        assertTrue(accountPassRight.isPresent());
        assertFalse(accountPassWrong.isPresent());
        assertThat(accountPassRight.get(), is(accountExisting));
        verify(emMock, times(2)).detach(accountExisting);
    }

    @Test
    public void getAccountByTokenHash() {
        Optional<Account> accountTokenValid = accountStore.getAccountByTokenHash(TOKEN_HASH_VALID);
        Optional<Account> accountTokenInvalid = accountStore.getAccountByTokenHash(TOKEN_HASH_INVALID);

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
        assertThat(accountStore.getAllAccounts(), is(arrayList));
        verify(emMock).detach(accountExisting);
    }

    @Test
    public void changeExistingAccountStatus() {
        accountStore.changeAccountStatus(accountMockID, true);
        verify(emMock).find(Account.class, accountMockID);
        verify(accountMock).setActive(true);
    }

    @Test(expected = NoResultException.class)
    public void changeNonExistingAccountStatus() {
        accountStore.changeAccountStatus(accountNonExistingID, true);
    }

    @Test
    public void changeExistingAccountPassword() throws UnsecurePasswordException {
        accountStore.changeAccountPassword(accountMockID, PASSWORD_VALID);
        verify(encryptorMock).generate(PASSWORD_VALID);
        verify(emMock).find(Account.class, accountMockID);
        verify(accountMock).setPassHash(PASS_HASH_VALID);
    }

    @Test(expected = NoResultException.class)
    public void changeNonExistingAccountPassword() throws UnsecurePasswordException {
        accountStore.changeAccountPassword(accountNonExistingID, PASSWORD_VALID);
    }

    @Test
    public void changeSelfAccountPassword() throws UnsecurePasswordException {
        accountStore.changeSelfAccountPassword(PASSWORD_VALID);
        verify(encryptorMock).generate(PASSWORD_VALID);
        verify(accountMock).setPassHash(PASS_HASH_VALID);
    }

    @Test
    public void changeAccountPasswordFail() {
        for (String password : badPasswords) {
            try {
                accountStore.changeAccountPassword(accountMockID, password);
            } catch (UnsecurePasswordException e) {
                //as expected
            }
            try {
                accountStore.changeSelfAccountPassword(password);
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
        accountStore.changeAccountEmail(accountMockID, "email");
        verify(emMock).find(Account.class, accountMockID);
        verify(accountMock).setEmail("email");
    }

    @Test(expected = NoResultException.class)
    public void changeNonExistingAccountEmail() {
        accountStore.changeAccountEmail(accountNonExistingID, "email");
    }

    @Test
    public void changeSelfAccountEmail() {
        accountStore.changeSelfAccountEmail("email");
        verify(accountMock).setEmail("email");
    }

    @Test
    public void addRoleToExistingAccount() {
        assertTrue(accountExisting.getRoles().isEmpty());

        accountStore.addRoleToAccount(accountExistingID, MODERATOR);
        assertThat(accountExisting.getRoles().size(), is(1));
        assertTrue(accountExisting.getRoles().contains(MODERATOR));

        accountStore.addRoleToAccount(accountExistingID, MODERATOR);
        assertThat(accountExisting.getRoles().size(), is(1));
        assertTrue(accountExisting.getRoles().contains(MODERATOR));

        verify(emMock, times(2)).find(Account.class, accountExistingID);
    }

    @Test(expected = NoResultException.class)
    public void addRoleToNonExistingAccount() {
        accountStore.addRoleToAccount(accountNonExistingID, MODERATOR);
    }

    @Test
    public void removeRoleFromExistingAccount() {
        assertTrue(accountExisting.getRoles().isEmpty());
        accountExisting.getRoles().add(ADMIN);
        accountExisting.getRoles().add(MODERATOR);

        accountStore.removeRoleFromAccount(accountExistingID, ADMIN);

        assertThat(accountExisting.getRoles().size(), is(1));
        assertTrue(accountExisting.getRoles().contains(MODERATOR));
        verify(emMock).find(Account.class, accountExistingID);
    }

    @Test(expected = NoResultException.class)
    public void removeRoleFromNonExistingAccount() {
        accountStore.removeRoleFromAccount(accountNonExistingID, MODERATOR);
    }

    @Test
    public void setNewRolesToExistingAccount() {
        Set<Roles> roles = new HashSet<>();
        accountStore.setNewRolesToAccount(accountExistingID, roles);
        verify(emMock).find(Account.class, accountExistingID);
        assertThat(accountExisting.getRoles(), sameInstance(roles));
    }

    @Test(expected = NoResultException.class)
    public void setNewRolesToNonExistingAccount() {
        Set<Roles> roles = new HashSet<>();
        accountStore.setNewRolesToAccount(accountNonExistingID, roles);
    }
}