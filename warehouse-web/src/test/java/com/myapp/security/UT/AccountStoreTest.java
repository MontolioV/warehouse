package com.myapp.security.UT;

import com.myapp.security.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;

import static com.myapp.security.Roles.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
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
    private Account accountTest;

    @Before
    public void setUp() throws Exception {
        ArrayList<Roles> roles = new ArrayList<>();
        roles.add(ADMIN);
        accountTest = new Account(0, "test", "PassWord1", "test", new ArrayList<>(), roles);
        when(emMock.merge(accountTest)).thenReturn(accountTest);
        when(encryptorMock.generate("test")).thenReturn("hash");
    }

    @Test
    public void createAccount() throws LoginExistsException, UnsecurePasswordException {
        accountStore.createAccount(accountTest);
        verify(emMock).persist(accountTest);
        assertThat(accountTest.getPassHash(), is("hash"));
        assertThat(accountTest.isActive(), is(false));
        assertThat(accountTest.getRoles().size(), is(2));
        assertTrue(accountTest.getRoles().contains(USER));
    }

    @Test
    public void createExistingAccount() throws UnsecurePasswordException {
        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1, "existing", "existing", "existing", new ArrayList<>(), new ArrayList<>()));
        TypedQuery<Account> customQueryMock = mock(TypedQuery.class);
        when(emMock.createNamedQuery(Account.GET_BY_LOGIN, Account.class)).thenReturn(customQueryMock);
        when(customQueryMock.setParameter("login", "existing")).thenReturn(customQueryMock);
        when(customQueryMock.getResultList()).thenReturn(accounts);

        accountTest.setLogin("existing");
        try {
            accountStore.createAccount(accountTest);
        } catch (LoginExistsException e) {
            //as expected
        }

        verify(emMock, never()).persist(accountTest);
    }

    @Test
    public void createAccountWithBadPass() throws LoginExistsException {
        String[] passwords = {"Shrt12", "NoNumerals", "nocapitals238",};
        for (String password : passwords) {
            accountTest.setPassHash(password);
            try {
                accountStore.createAccount(accountTest);
            } catch (UnsecurePasswordException e) {
                //as expected
            }
        }

        verify(emMock, never()).persist(accountTest);
    }


    @Test
    public void findAccountByLogin() {
        TypedQuery<Account> customQueryMock = mock(TypedQuery.class);
        when(emMock.createNamedQuery(anyString(), Account.class)).thenReturn(customQueryMock);
        when(customQueryMock.setParameter("login", anyString())).thenReturn(customQueryMock);
        when(customQueryMock.getSingleResult()).thenReturn(any(Account.class));

        Account account = accountStore.findAccountByLogin(anyString());
        assertThat(account, is(any(Account.class)));
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
        Account account = accountStore.changeAccountStatus(accountTest, true);
        verify(emMock).merge(accountTest);
        assertThat(account.isActive(), is(true));
    }

    @Test
    public void changeAccountPassword() throws UnsecurePasswordException {
        Account account = accountStore.changeAccountPassword(accountTest, "PassWord132");
        verify(emMock).merge(accountTest);
        assertThat(account.getPassHash(), is("hash"));
    }
    @Test
    public void changeAccountPasswordFail() {
        String[] passwords = {"Shrt12", "NoNumerals", "nocapitals238",};
        for (String password : passwords) {
            try {
                accountStore.changeAccountPassword(accountTest, password);
            } catch (UnsecurePasswordException e) {
                //as expected
            }
        }
        verify(emMock, never()).merge(accountTest);
    }

    @Test
    public void changeAccountEmail() {
        Account account = accountStore.changeAccountEmail(accountTest, anyString());
        verify(emMock).merge(accountTest);
        assertThat(account.getEmail(), is(anyString()));
    }

    @Test
    public void addRoleToAccount() {
        Account account = accountStore.addRoleToAccount(accountTest, MODERATOR);
        assertThat(account.getRoles().size(), is(2));
        assertTrue(account.getRoles().contains(MODERATOR));

        accountStore.addRoleToAccount(accountTest, MODERATOR);
        assertThat(account.getRoles().size(), is(2));
        verify(emMock).merge(accountTest);
    }

    @Test
    public void removeRoleFromAccount() {
        Account account = accountStore.addRoleToAccount(accountTest, ADMIN);
        verify(emMock).merge(accountTest);
        assertThat(account.getRoles().size(), is(0));
        assertFalse(account.getRoles().contains(ADMIN));
    }
}