package com.myapp.administration.UT;

import com.myapp.administration.UserManagementController;
import com.myapp.security.Account;
import com.myapp.security.AccountStore;
import com.myapp.security.Roles;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.*;

import static com.myapp.utils.TestSecurityConstants.LOGIN_INVALID;
import static com.myapp.utils.TestSecurityConstants.LOGIN_VALID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 06.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserManagementControllerTest {
    @InjectMocks
    private UserManagementController controller;
    @Mock
    private AccountStore asMock;
    @Mock
    private FacesContext facesContext;
    private Account fetchedAccountMock;

    @Before
    public void setUp() throws Exception {
        fetchedAccountMock = mock(Account.class);

        Set<Roles> roles = new HashSet<>();
        roles.add(Roles.ADMIN);
        when(fetchedAccountMock.isActive()).thenReturn(true);
        when(fetchedAccountMock.getRoles()).thenReturn(roles);
        when(asMock.getAccountByLogin(LOGIN_VALID)).thenReturn(Optional.of(fetchedAccountMock));

        controller.setLogin(LOGIN_VALID);
    }

    @Test
    public void fetchAccounts() {
        List<Account> accounts = new ArrayList<>();
        when(asMock.getAllAccounts()).thenReturn(accounts);

        controller.fetchAccounts();
        List<Account> result = controller.getAccountList();
        assertThat(result, sameInstance(accounts));
    }

    @Test
    public void fetchSingleAccountSuccess() {
        controller.fetchSingleAccount();
        fetchSingleAccountSuccessAsserts();
    }

    @Test
    public void fetchSingleAccountFail() {
        when(asMock.getAccountByLogin(LOGIN_INVALID)).thenReturn(Optional.empty());

        controller.setLogin(LOGIN_INVALID);
        controller.fetchSingleAccount();
        Account resultFail = controller.getSingleAccount();
        List<Account> result = controller.getAccountList();

        assertNull(resultFail);
        assertNull(result);
        verify(facesContext).addMessage(eq("usersListForm:login"), any(FacesMessage.class));
    }

    @Test
    public void updateAccount() {
        Account oldAccountMock = mock(Account.class);
        when(oldAccountMock.getLogin()).thenReturn(LOGIN_VALID);

        Set<Roles> rolesList = new HashSet<>();
        rolesList.add(Roles.ADMIN);
        controller.setActive(true);
        controller.setRoles(rolesList);
        controller.setSingleAccount(oldAccountMock);
        controller.setLogin("changed login");
        controller.updateAccount();

        verify(asMock).changeAccountStatus(oldAccountMock.getId(), true);
        verify(asMock).setNewRolesToAccount(oldAccountMock.getId(), rolesList);
        fetchSingleAccountSuccessAsserts();
    }

    private void fetchSingleAccountSuccessAsserts() {
        Account resultSuccess = controller.getSingleAccount();
        List<Account> result = controller.getAccountList();

        assertThat(resultSuccess, sameInstance(fetchedAccountMock));
        assertThat(result.size(), is(1));
        assertTrue(result.contains(fetchedAccountMock));
        assertTrue(controller.isActive());
        assertTrue(controller.getRoles().contains(Roles.ADMIN));
        assertThat(controller.getRoles().size(), is(1));
        verify(facesContext, never()).addMessage(anyString(), any(FacesMessage.class));
    }
}