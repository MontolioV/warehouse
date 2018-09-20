package com.myapp.validation.UT;

import com.myapp.security.Account;
import com.myapp.security.AccountStore;
import com.myapp.validation.EmailValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.validator.ValidatorException;
import java.util.Optional;

import static com.myapp.utils.TestSecurityConstants.EMAIL_VALID;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 20.09.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailValidatorTest {
    @InjectMocks
    private EmailValidator validator;
    @Mock
    private AccountStore asMock;
    @Mock
    private Account accountMock;
    private String unknownEmail = "unknownEmail@unknownEmail";

    @Test(expected = ValidatorException.class)
    public void validateExisting() {
        when(asMock.getAccountByEmail(EMAIL_VALID)).thenReturn(Optional.of(accountMock));
        validator.validate(null, null, EMAIL_VALID);
    }

    @Test
    public void validateNew() {
        when(asMock.getAccountByEmail(unknownEmail)).thenReturn(Optional.empty());
        validator.validate(null, null, unknownEmail);
    }

    @Test(expected = ValidatorException.class)
    public void validateEmpty() {
        validator.validate(null, null, "");
    }

    @Test(expected = ValidatorException.class)
    public void validateNull() {
        validator.validate(null, null, null);
    }
}