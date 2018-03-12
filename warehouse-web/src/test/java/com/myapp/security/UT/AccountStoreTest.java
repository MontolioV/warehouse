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
import java.util.Optional;

import static com.myapp.security.Roles.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 05.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountStoreTest {
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
    private String password = "PassWord1";
    private String wrongPassword = "wrongPassword";
    private String passHash = "hash";
    private String passBadHash = "badHash";
    private String[] badPasswords = {"Shrt12", "NoNumerals", "nocapitals238",};

    @Before
    public void setUp() throws Exception {
        accountNew = new Account(1L, "test", password, "test", new ArrayList<>(), new ArrayList<>());
        accountExisting = new Account(0L, "existing", passHash, "existing", new ArrayList<>(), new ArrayList<>());

        when(encryptorMock.generate(password)).thenReturn(passHash);
        when(encryptorMock.generate(wrongPassword)).thenReturn(passBadHash);
        for (String badPassword : badPasswords) {
            when(encryptorMock.generate(badPassword)).thenReturn(passBadHash);
        }
        when(encryptorMock.verify(password, passHash)).thenReturn(true);
        when(encryptorMock.verify(wrongPassword, passHash)).thenReturn(false);

        when(emMock.merge(accountNew)).thenReturn(accountNew);
        when(emMock.merge(accountExisting)).thenReturn(accountExisting);
        when(emMock.merge(accountMock)).thenReturn(accountMock);

        TypedQuery<Account> getByLoginQueryMock = mock(TypedQuery.class);
        when(emMock.createNamedQuery(Account.GET_BY_LOGIN, Account.class)).thenReturn(getByLoginQueryMock);

        ArrayList<Account> accountsEmpty = new ArrayList<>();
        TypedQuery<Account> uniqueLoginQueryMock = mock(TypedQuery.class);
        when(getByLoginQueryMock.setParameter("login", accountNew.getLogin())).thenReturn(uniqueLoginQueryMock);
        when(uniqueLoginQueryMock.getResultList()).thenReturn(accountsEmpty);
        when(uniqueLoginQueryMock.getSingleResult()).thenThrow(new NoResultException());

        ArrayList<Account> accountsNotEmpty = new ArrayList<>();
        accountsNotEmpty.add(accountExisting);
        TypedQuery<Account> existingLoginQueryMock = mock(TypedQuery.class);
        when(getByLoginQueryMock.setParameter("login", accountExisting.getLogin())).thenReturn(existingLoginQueryMock);
        when(existingLoginQueryMock.getResultList()).thenReturn(accountsNotEmpty);
        when(existingLoginQueryMock.getSingleResult()).thenReturn(accountExisting);
    }

    @Test
    public void createAccount() throws LoginExistsException, UnsecurePasswordException {
        accountStore.createAccount(accountNew);
        verify(emMock).persist(accountNew);

        assertThat(accountNew.getId(), is(1L));
        assertThat(accountNew.getLogin(), is("test"));
        assertThat(accountNew.getPassHash(), is(passHash));
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
    }

    @Test
    public void getAccountByLogin() {
        Optional<Account> accountNotExists = accountStore.getAccountByLogin(accountNew.getLogin());
        Optional<Account> accountExists = accountStore.getAccountByLogin(accountExisting.getLogin());

        assertFalse(accountNotExists.isPresent());
        assertTrue(accountExists.isPresent());
        assertThat(accountExists.get(), is(accountExisting));
        verify(emMock, times(2)).createNamedQuery(Account.GET_BY_LOGIN, Account.class);
    }

    @Test
    public void getAccountByLoginAndPassword() {
        Optional<Account> accountPassRight = accountStore.getAccountByLoginAndPassword(accountExisting.getLogin(), password);
        Optional<Account> accountPassWrong = accountStore.getAccountByLoginAndPassword(accountExisting.getLogin(), wrongPassword);

        verify(encryptorMock, times(2)).verify(any(String.class), any(String.class));
        assertTrue(accountPassRight.isPresent());
        assertFalse(accountPassWrong.isPresent());
        assertThat(accountPassRight.get(), is(accountExisting));
    }

    @Test
    public void getAllAccounts() {
        TypedQuery<Account> customQueryMock = mock(TypedQuery.class);
        ArrayList<Account> arrayList = new ArrayList<>();
        when(emMock.createNamedQuery(Account.GET_ALL, Account.class)).thenReturn(customQueryMock);
        when(customQueryMock.getResultList()).thenReturn(arrayList);
        assertThat(accountStore.getAllAccounts(), is(arrayList));
    }

    @Test
    public void changeAccountStatus() {
        accountStore.changeAccountStatus(accountMock, true);
        verify(accountMock).setActive(true);
        verify(emMock).merge(accountMock);
    }

    @Test
    public void changeAccountPassword() throws UnsecurePasswordException {
        accountStore.changeAccountPassword(accountMock, password);
        verify(encryptorMock).generate(password);
        verify(accountMock).setPassHash(passHash);
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
            verify(accountMock, never()).setPassHash(passBadHash);
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
}