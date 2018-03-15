package security.UT;

import com.myapp.security.Encryptor;
import org.glassfish.soteria.identitystores.hash.Pbkdf2PasswordHashImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class EncryptorTest implements SecurityConstants{
    @Mock
    private Pbkdf2PasswordHashImpl hashMock;
    @InjectMocks
    private Encryptor encryptor;

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