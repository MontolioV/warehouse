package com.myapp.security.UT;

import com.myapp.security.Account;
import com.myapp.security.Roles;
import com.myapp.security.Token;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * <p>Created by MontolioV on 11.04.18.
 */
public class AccountTest {

    @Test
    public void serialization() throws IOException, ClassNotFoundException {
        List<Token> tokens = new ArrayList<>();
        Set<Roles> roles = new HashSet<>();
        tokens.add(new Token());
        roles.add(Roles.ADMIN);
        Account account = new Account(1, "test", "test", "test", tokens, roles);

        try (PipedInputStream inputStream = new PipedInputStream();
             PipedOutputStream outputStream = new PipedOutputStream(inputStream);
             ObjectOutputStream oos = new ObjectOutputStream(outputStream);
             ObjectInputStream ois = new ObjectInputStream(inputStream))
        {
            oos.writeObject(account);
            Account restored = (Account) ois.readObject();

            assertThat(restored, is(account));
        }
    }
}