package com.myapp.security.UT;

import com.myapp.security.EncryptorGlassfishPbkdf2;
import org.glassfish.soteria.identitystores.hash.Pbkdf2PasswordHashImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.myapp.utils.TestSecurityConstants.PASSWORD_VALID;
import static com.myapp.utils.TestSecurityConstants.PASS_HASH_VALID;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class EncryptorGlassfishPbkdf2Test implements CommonChecks {
    @InjectMocks
    private EncryptorGlassfishPbkdf2 encryptor;
    @Mock
    private Pbkdf2PasswordHashImpl hashMock;

    @Test
    public void generate() {
        encryptor.generate(PASSWORD_VALID);
        verify(hashMock).generate(PASSWORD_VALID.toCharArray());
    }

    @Test
    public void verifyT() {
        encryptor.verify(PASSWORD_VALID, PASS_HASH_VALID);
        verify(hashMock).verify(PASSWORD_VALID.toCharArray(), PASS_HASH_VALID);
    }
}