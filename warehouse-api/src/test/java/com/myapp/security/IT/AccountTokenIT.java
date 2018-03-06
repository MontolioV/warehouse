package com.myapp.security.IT;

import com.myapp.security.Account;
import com.myapp.security.Roles;
import com.myapp.security.Token;
import com.myapp.security.TokenType;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AccountTokenIT extends WithEmbeddedDB{

    @Test
    public void success() {
        Token expectedToken = new Token(0, "sajdaj", TokenType.REMEMBER_ME, new Date());
        ArrayList<Roles> roles = new ArrayList<>();
        roles.add(Roles.ADMIN);
        String s126 = StringUtils.repeat("0", 128);
        Account expectedAccount = new Account(0, "test", s126, "test@test.com", new ArrayList<>(), roles);
        expectedAccount.addToken(expectedToken);

        transaction.begin();
        try {
            em.persist(expectedAccount);
        } catch (ConstraintViolationException e) {
            Iterator<ConstraintViolation<?>> cvIterator = ((ConstraintViolationException) e).getConstraintViolations().iterator();
            while (cvIterator.hasNext()) {
                ConstraintViolation<?> nextViolation = cvIterator.next();
                System.err.println(nextViolation);
                System.err.println(nextViolation.getMessage());
            }
            throw e;
        }
        transaction.commit();

        Account realAccount = em.createNamedQuery(Account.GET_ALL, Account.class).getSingleResult();
        Token realToken = em.createNamedQuery(Token.GET_ALL, Token.class).getSingleResult();

        assertThat(realAccount, is(expectedAccount));
        assertThat(realToken, is(expectedToken));
    }
}