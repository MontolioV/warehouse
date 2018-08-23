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
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.security.Principal;
import java.util.Optional;

import static com.myapp.utils.TestSecurityConstants.LOGIN_VALID;
import static com.myapp.utils.TestSecurityConstants.PASSWORD_VALID;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("javax.faces.component.UIComponent")
@PowerMockIgnore("javax.security.auth.Subject")
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
    private UIComponent newPassInputMock;

    @Before
    public void setUp() {
        newPassInputMock = mock(UIComponent.class);
        controller.setNewPasswordInput(newPassInputMock);

        account = new Account();
        when(fcMock.getExternalContext()).thenReturn(ecMock);
        when(ecMock.getUserPrincipal()).thenReturn(principalMock);
        when(principalMock.getName()).thenReturn(LOGIN_VALID);
        when(asMock.getAccountByLogin(LOGIN_VALID)).thenReturn(Optional.of(account));
        when(newPassInputMock.getClientId()).thenReturn("newID");
    }

    @Test
    public void fetchSelfAccount() {
        controller.fetchSelfAccount();
        assertThat(controller.getSelfAccount(), sameInstance(account));
    }

    @Test
    public void changePasswordSuccess() throws UnsecurePasswordException {
        controller.setNewPassword(PASSWORD_VALID);
        controller.changePassword();

        verify(asMock).changeSelfAccountPassword(PASSWORD_VALID);
        verify(fcMock).addMessage(eq(null), any(FacesMessage.class));
        assertThat(controller.getSelfAccount(), sameInstance(account));
    }

    @Test
    public void changePasswordFail() throws UnsecurePasswordException {
        controller.setNewPassword(PASSWORD_VALID);
        Mockito.doThrow(new UnsecurePasswordException()).when(asMock).changeSelfAccountPassword(PASSWORD_VALID);
        controller.changePassword();

        verify(asMock).changeSelfAccountPassword(PASSWORD_VALID);
        verify(fcMock).addMessage(eq(newPassInputMock.getClientId()), any(FacesMessage.class));
    }


    public void changeEmail() {
        controller.setEmail(anyString());
        controller.changeEmail();

        verify(asMock).changeSelfAccountEmail(anyString());
        verify(fcMock).addMessage(eq(null), any(FacesMessage.class));
        assertThat(controller.getSelfAccount(), sameInstance(account));
    }
}