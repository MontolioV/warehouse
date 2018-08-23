package com.myapp.validation.UT;

import com.myapp.security.Account;
import com.myapp.security.AccountStore;
import com.myapp.validation.RightPasswordValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.security.Principal;
import java.util.Optional;

import static com.myapp.utils.TestSecurityConstants.*;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 23.08.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class RightPasswordValidatorTest {
    @InjectMocks
    private RightPasswordValidator validator;
    @Mock
    private AccountStore asMock;
    @Mock
    private FacesContext fcMock;
    @Mock
    private ExternalContext ecMock;
    @Mock
    private Principal principalMock;
    @Mock
    private Account accountMock;

    @Before
    public void setUp() {
        when(fcMock.getExternalContext()).thenReturn(ecMock);
        when(ecMock.getUserPrincipal()).thenReturn(principalMock);
        when(principalMock.getName()).thenReturn(LOGIN_VALID);
        when(asMock.getAccountByLoginAndPassword(LOGIN_VALID, PASSWORD_VALID)).thenReturn(Optional.of(accountMock));
        when(asMock.getAccountByLoginAndPassword(LOGIN_VALID, PASSWORD_INVALID)).thenReturn(Optional.empty());
    }

    @Test
    public void validateSuccess() {
        validator.validate(fcMock, null, PASSWORD_VALID);
    }

    @Test(expected = ValidatorException.class)
    public void validateFail() {
        validator.validate(fcMock, null, PASSWORD_INVALID);
    }
}