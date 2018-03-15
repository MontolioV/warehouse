package com.myapp.security.UT;

import com.myapp.security.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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

    @Test
    public void doRegister() throws LoginExistsException, UnsecurePasswordException {
        controller.register();
        verify(accountStore).createAccount(any(Account.class));
        verify(facesContext).addMessage(anyString(), any(FacesMessage.class));
    }
}