package com.myapp.security.IT;

import com.myapp.security.Account;
import com.myapp.security.Roles;
import com.myapp.security.Token;
import com.myapp.security.TokenType;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AccountTokenIT extends WithEmbeddedDB{

    @Test
    public void success() {
        Token expectedToken = new Token(0, "sajdaj", TokenType.REMEMBER_ME, null, Instant.now());
        ArrayList<Roles> roles = new ArrayList<>();
        roles.add(Roles.ADMIN);
        Account expectedAccount = new Account(0, "test", "akdmsaldk", "test@test.com", new ArrayList<>(), roles);
        expectedAccount.addToken(expectedToken);
        expectedToken.setAccount(expectedAccount);

        transaction.begin();
        em.persist(expectedAccount);
        transaction.commit();

        Account realAccount = em.createNamedQuery(Account.GET_ALL, Account.class).getSingleResult();
        Token realToken = em.createNamedQuery(Token.GET_ALL, Token.class).getSingleResult();

        assertThat(realAccount, is(expectedAccount));
        assertThat(realToken, is(expectedToken));
    }
}