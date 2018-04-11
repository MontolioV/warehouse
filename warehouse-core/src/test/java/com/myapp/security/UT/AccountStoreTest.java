package com.myapp.security.UT;

import com.myapp.security.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
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
import static org.mockito.Matchers.anyString;
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
    private Account accountMock;
    private Account accountNew;
    private Account accountExisting;
    private String[] badPasswords = {"Shrt12", "NoNumerals", "nocapitals238",};

    @Before
    public void setUp() throws Exception {
        accountNew = new Account(1L, "test", PASSWORD_VALID, "test", new ArrayList<>(), new HashSet<>());
        accountExisting = new Account(0L, "existing", PASS_HASH_VALID, "existing", new ArrayList<>(), new HashSet<>());

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
    }

    @Test
    public void createAccount() throws LoginExistsException, UnsecurePasswordException {
        accountStore.createAccount(accountNew);
        verify(emMock).persist(accountNew);
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
    public void changeAccountStatus() {
        accountStore.changeAccountStatus(accountMock, true);
        verify(accountMock).setActive(true);
        verify(emMock).merge(accountMock);
    }

    @Test
    public void changeAccountPassword() throws UnsecurePasswordException {
        accountStore.changeAccountPassword(accountMock, PASSWORD_VALID);
        verify(encryptorMock).generate(PASSWORD_VALID);
        verify(accountMock).setPassHash(PASS_HASH_VALID);
        verify(emMock).merge(accountMock);
    }

    @Test
    public void changeAccountPasswordFail() {
        for (String password : badPasswords) {
            try {
                accountStore.changeAccountPassword(accountMock, password);
            } catch (UnsecurePasswordException e) {
                //as expected
            }
            verify(encryptorMock, never()).generate(password);
            verify(accountMock, never()).setPassHash(PASS_HASH_INVALID);
            verify(emMock, never()).merge(accountMock);
        }
    }

    @Test
    public void changeAccountEmail() {
        accountStore.changeAccountEmail(accountMock, anyString());
        verify(accountMock).setEmail(anyString());
        verify(emMock).merge(accountMock);
    }

    @Test
    public void addRoleToAccount() {
        Account account = accountStore.addRoleToAccount(accountNew, MODERATOR);
        assertThat(account.getRoles().size(), is(1));
        assertTrue(account.getRoles().contains(MODERATOR));

        account = accountStore.addRoleToAccount(accountNew, MODERATOR);
        assertThat(account.getRoles().size(), is(1));

        verify(emMock).merge(accountNew);
    }

    @Test
    public void removeRoleFromAccount() {
        accountNew.getRoles().add(ADMIN);
        Account account = accountStore.removeRoleFromAccount(accountNew, ADMIN);
        
        assertThat(account.getRoles().size(), is(0));
        assertFalse(account.getRoles().contains(ADMIN));

        for (Roles role : Roles.values()) {
            accountStore.removeRoleFromAccount(accountNew, role);
        }

        assertThat(account.getRoles().size(), is(0));
        verify(emMock).merge(accountNew);
    }

    @Test
    public void setNewRolesToAccount() {
        Set<Roles> roles = new HashSet<>();
        accountStore.setNewRolesToAccount(accountExisting, roles);
        verify(emMock).merge(accountExisting);
        assertThat(accountExisting.getRoles(), sameInstance(roles));
    }
}