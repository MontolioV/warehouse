package com.myapp.security.UT;

import com.myapp.security.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;

import static com.myapp.utils.TestSecurityConstants.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 13.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class RegistrationControllerTest {
    @InjectMocks
    private RegistrationController controller;
    @Mock
    private AccountStore asMock;
    @Mock
    private FacesContext fcMock;
    @Mock
    private ExternalContext ecMock;
    @Mock
    private Account accountMock;
    private String webAppAddress = "webAppAddress";

    @Before
    public void setUp() {
        when(fcMock.getExternalContext()).thenReturn(ecMock);
        controller.setWebAppAddress(webAppAddress);
    }

    @Test
    public void doRegistration() throws LoginExistsException, UnsecurePasswordException, IOException {
        Account accountNewMock = mock(Account.class);
        when(asMock.createAccount(accountMock)).thenReturn(accountNewMock);
        when(accountNewMock.getLogin()).thenReturn(LOGIN_VALID);

        controller.registration();
        verify(asMock).createAccount(any(Account.class));
        verify(fcMock).addMessage(anyString(), any(FacesMessage.class));
        verify(ecMock).redirect(webAppAddress + "rs/activation/" + LOGIN_VALID + "/send-email");
    }

    @Test
    public void passwordConfirmationSuccess() {
        controller.setPassword(PASSWORD_VALID);
        controller.setPasswordConfirm(PASSWORD_VALID);
        controller.passwordConfirmation();

        verify(accountMock).setPassHash(PASSWORD_VALID);
        verify(fcMock, never()).addMessage(anyString(), any(FacesMessage.class));
    }

    @Test
    public void passwordConfirmationFail() {
        controller.setPassword(PASSWORD_VALID);
        controller.setPasswordConfirm(PASSWORD_INVALID);
        controller.passwordConfirmation();

        verify(accountMock, never()).setPassHash(PASSWORD_VALID);
        verify(accountMock, never()).setPassHash(PASSWORD_INVALID);
        verify(fcMock).addMessage(anyString(), any(FacesMessage.class));
    }
}