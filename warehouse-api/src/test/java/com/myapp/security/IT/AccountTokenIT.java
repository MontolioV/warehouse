package com.myapp.security.IT;

import com.myapp.security.Account;
import com.myapp.security.Roles;
import com.myapp.security.Token;
import com.myapp.security.TokenType;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AccountTokenIT extends WithEmbeddedDB{
    private Instant instant;
    private Token expectedToken;
    private Account expectedAccount;
    private Token expiringToken;
    private List<Token> tokensExpected;

    @Test
    public void success() {
        instant = Instant.now().plus(14, ChronoUnit.DAYS);
        expectedToken = new Token(0, "sajdaj", TokenType.REMEMBER_ME, new Date(), Date.from(instant));
        ArrayList<Roles> roles = new ArrayList<>();
        roles.add(Roles.ADMIN);
        String s126 = StringUtils.repeat("0", 128);
        expectedAccount = new Account(0, "test", s126, "test@test.com", new ArrayList<>(), roles);
        expectedAccount.addToken(expectedToken);

        instant = Instant.now().plus(1, ChronoUnit.DAYS);
        expiringToken = new Token(0, "sajdaj1231", TokenType.REMEMBER_ME, new Date(), Date.from(instant));
        tokensExpected = new ArrayList<>();
        tokensExpected.add(expectedToken);
        tokensExpected.add(expiringToken);

        transaction.begin();
        try {
            em.persist(expectedAccount);
            em.persist(expiringToken);
        } catch (ConstraintViolationException e) {
            Iterator<ConstraintViolation<?>> cvIterator = e.getConstraintViolations().iterator();
            while (cvIterator.hasNext()) {
                ConstraintViolation<?> nextViolation = cvIterator.next();
                System.err.println(nextViolation);
                System.err.println(nextViolation.getMessage());
            }
            throw e;
        }
        transaction.commit();

    }

    private void queryTests() {
        Account accountFromAll = em.createNamedQuery(Account.GET_ALL, Account.class)
                .getSingleResult();
        Account accountFromLogin = em.createNamedQuery(Account.GET_BY_LOGIN, Account.class)
                .setParameter("login",expectedAccount.getLogin())
                .getSingleResult();
        assertThat(expectedAccount, allOf(is(accountFromAll), is(accountFromLogin)));

        List<Token> tokensFromAll = em.createNamedQuery(Token.GET_ALL, Token.class)
                .getResultList();
        Token tokenFromLogin = em.createNamedQuery(Token.GET_ALL, Token.class)
                .getSingleResult();
        assertThat(tokensFromAll, is(tokensExpected));
        assertThat(tokenFromLogin, is(expectedToken));

        instant = Instant.now().plus(10, ChronoUnit.DAYS);
        em.createNamedQuery(Token.DELETE_EXPIRED_TO_DATE)
                .setParameter("date", Date.from(instant)).executeUpdate();
        tokensExpected.remove(expiringToken);
        tokensFromAll = em.createNamedQuery(Token.GET_ALL, Token.class)
                .getResultList();
        assertThat(tokensFromAll, is(tokensExpected));

        em.createNamedQuery(Token.DELETE_BY_HASH)
                .setParameter("hash", expectedToken.getTokenHash()).executeUpdate();
        tokensFromAll = em.createNamedQuery(Token.GET_ALL, Token.class)
                .getResultList();
        assertTrue(tokensFromAll.isEmpty());
    }
}