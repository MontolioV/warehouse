package com.myapp.security.UT;

import com.myapp.security.Encryptor;
import org.glassfish.soteria.identitystores.hash.Pbkdf2PasswordHashImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class EncryptorTest {
    @Mock
    private Pbkdf2PasswordHashImpl hashMock;
    @InjectMocks
    private Encryptor encryptor;
    private String pass = "test";
    private String hash = "hash";

    @Test
    public void generate() {
        encryptor.generate(pass);
        verify(hashMock).generate(pass.toCharArray());
    }

    @Test
    public void verifyT() {
        encryptor.verify(pass, hash);
        verify(hashMock).verify(pass.toCharArray(), hash);
    }
}