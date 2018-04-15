package com.myapp.administration.UT;

import com.myapp.administration.PersonalCabinetController;
import com.myapp.security.Account;
import com.myapp.security.AccountStore;
import com.myapp.security.UnsecurePasswordException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.security.Principal;
import java.util.Optional;

import static com.myapp.utils.TestSecurityConstants.*;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PersonalCabinetControllerTest {
    @InjectMocks
    private PersonalCabinetController controller;
    @Mock
    private AccountStore asMock;
    @Mock
    private FacesContext fcMock;
    @Mock
    private ExternalContext ecMock;
    @Mock
    private Principal principalMock;
    private Account account;

    @Before
    public void setUp() throws Exception {
        account = new Account();
        when(fcMock.getExternalContext()).thenReturn(ecMock);
        when(ecMock.getUserPrincipal()).thenReturn(principalMock);
        when(principalMock.getName()).thenReturn(LOGIN_VALID);
        when(asMock.getAccountByLogin(LOGIN_VALID)).thenReturn(Optional.of(account));
    }

    @Test
    public void fetchSelfAccount() {
        controller.fetchSelfAccount();
        assertThat(controller.getSelfAccount(), sameInstance(account));
    }

    @Test
    public void changePasswordSuccess() throws UnsecurePasswordException {
        controller.setPassword(PASSWORD_VALID);
        controller.setConfirmPassword(PASSWORD_VALID);
        controller.changePassword();

        verify(asMock).changeSelfAccountPassword(PASSWORD_VALID);
        verify(fcMock).addMessage(eq(null), any(FacesMessage.class));
        assertThat(controller.getSelfAccount(), sameInstance(account));
    }

    @Test
    public void changePasswordFail() throws UnsecurePasswordException {
        controller.setPassword(PASSWORD_VALID);
        controller.setConfirmPassword(PASSWORD_INVALID);
        controller.changePassword();

        verify(asMock, never()).changeSelfAccountPassword(anyString());
        verify(fcMock).addMessage(eq(null), any(FacesMessage.class));

        Mockito.doThrow(new UnsecurePasswordException()).when(asMock).changeSelfAccountPassword(PASSWORD_VALID);
        controller.setConfirmPassword(PASSWORD_VALID);
        controller.changePassword();

        verify(asMock).changeSelfAccountPassword(PASSWORD_VALID);
        verify(fcMock, times(2)).addMessage(eq(null), any(FacesMessage.class));
    }


    public void changeEmail() {
        controller.setEmail(anyString());
        controller.changeEmail();

        verify(asMock).changeSelfAccountEmail(anyString());
        verify(fcMock).addMessage(eq(null), any(FacesMessage.class));
        assertThat(controller.getSelfAccount(), sameInstance(account));
    }
}