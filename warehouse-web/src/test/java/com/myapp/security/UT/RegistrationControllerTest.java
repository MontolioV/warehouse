package com.myapp.security.UT;

import com.myapp.security.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import static com.myapp.utils.TestSecurityConstants.PASSWORD_INVALID;
import static com.myapp.utils.TestSecurityConstants.PASSWORD_VALID;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * <p>Created by MontolioV on 13.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class RegistrationControllerTest {
    @InjectMocks
    private RegistrationController controller;
    @Mock
    private AccountStore accountStore;
    @Mock
    private FacesContext facesContext;
    @Mock
    private Account accountMock;

    @Test
    public void doRegistration() throws LoginExistsException, UnsecurePasswordException {
        controller.registration();
        verify(accountStore).createAccount(any(Account.class));
        verify(facesContext).addMessage(anyString(), any(FacesMessage.class));
    }

    @Test
    public void passwordConfirmationSuccess() {
        controller.setPassword(PASSWORD_VALID);
        controller.setPasswordConfirm(PASSWORD_VALID);
        controller.passwordConfirmation();

        verify(accountMock).setPassHash(PASSWORD_VALID);
        verify(facesContext, never()).addMessage(anyString(), any(FacesMessage.class));
    }

    @Test
    public void passwordConfirmationFail() {
        controller.setPassword(PASSWORD_VALID);
        controller.setPasswordConfirm(PASSWORD_INVALID);
        controller.passwordConfirmation();

        verify(accountMock, never()).setPassHash(PASSWORD_VALID);
        verify(accountMock, never()).setPassHash(PASSWORD_INVALID);
        verify(facesContext).addMessage(anyString(), any(FacesMessage.class));
    }
}