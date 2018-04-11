package com.myapp.security.UT;

import com.myapp.security.Token;
import com.myapp.security.TokenType;
import org.junit.Test;

import java.io.*;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * <p>Created by MontolioV on 11.04.18.
 */
public class TokenTest {

    @Test
    public void serialization() throws IOException, ClassNotFoundException {
        Token token = new Token(1, "test", TokenType.REMEMBER_ME, new Date(), new Date());

        try (PipedInputStream inputStream = new PipedInputStream();
             PipedOutputStream outputStream = new PipedOutputStream(inputStream);
             ObjectOutputStream oos = new ObjectOutputStream(outputStream);
             ObjectInputStream ois = new ObjectInputStream(inputStream))
        {
            oos.writeObject(token);
            Token restored = (Token) ois.readObject();

            assertThat(restored, is(token));
        }
    }
}