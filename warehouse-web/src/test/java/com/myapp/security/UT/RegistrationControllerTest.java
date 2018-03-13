package com.myapp.security.UT;

import com.myapp.security.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
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

    @Test
    public void doRegister() throws LoginExistsException, UnsecurePasswordException {
        verify(accountStore).createAccount(any(Account.class));
    }
}