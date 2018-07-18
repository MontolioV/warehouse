package com.myapp.security.IT;

import com.myapp.WithEmbeddedDB;
import com.myapp.security.Account;
import com.myapp.security.Roles;
import com.myapp.security.Token;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.myapp.TestUtils.showConstraintViolations;
import static com.myapp.security.Account.EMAIL_PARAM;
import static com.myapp.security.Account.LOGIN_PARAM;
import static com.myapp.security.Token.DATE_PARAM;
import static com.myapp.security.Token.HASH_PARAM;
import static com.myapp.security.TokenType.REMEMBER_ME;
import static com.myapp.utils.TestSecurityConstants.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AccountToken_DB_IT extends WithEmbeddedDB {
    private Instant instant;
    private Token expectedToken;
    private Account expectedAccount;
    private Token expiringToken;
    private List<Token> tokensExpected;
    private HashSet<Roles> roles;

    @Before
    public void setUp() throws Exception {
        roles = new HashSet<>();
        roles.add(Roles.ADMIN);
        expectedAccount = new Account(0, LOGIN_VALID, PASS_HASH_VALID, EMAIL_VALID, new ArrayList<>(), roles);

        instant = Instant.now().plus(14, ChronoUnit.DAYS);
        expectedToken = new Token(0, TOKEN_HASH_VALID, REMEMBER_ME, new Date(), Date.from(instant));
        expectedAccount.addToken(expectedToken);

        instant = Instant.now().plus(1, ChronoUnit.DAYS);
        expiringToken = new Token(0, TOKEN_HASH_INVALID, REMEMBER_ME, new Date(), Date.from(instant));
        tokensExpected = new ArrayList<>();
        tokensExpected.add(expectedToken);
        tokensExpected.add(expiringToken);
        tokensExpected.sort(Comparator.comparing(Token::getTokenHash));

        transaction.begin();
        try {
            em.persist(expectedAccount);
            em.persist(expiringToken);
        } catch (ConstraintViolationException e) {
            showConstraintViolations(e);
        }
        transaction.commit();
    }

    @After
    public void tearDown() throws Exception {
        transaction.begin();
        em.createNamedQuery(Token.DELETE_BY_HASH).setParameter(HASH_PARAM, TOKEN_HASH_INVALID).executeUpdate();
        em.createNamedQuery(Account.GET_ALL, Account.class).getResultList().forEach(em::remove);
        transaction.commit();

        assertTrue(em.createNamedQuery(Account.GET_ALL, Account.class).getResultList().isEmpty());
        assertTrue(em.createNamedQuery(Token.GET_ALL, Token.class).getResultList().isEmpty());
    }

    @Test
    public void persist() throws Exception {
        assertThat(expectedToken.getId(), is(not(equalTo(expiringToken.getId()))));

        queryTests();
    }

    private void queryTests() {
        Account accountFromAll = em.createNamedQuery(Account.GET_ALL, Account.class)
                .getSingleResult();
        Account accountFromLogin = em.createNamedQuery(Account.GET_BY_LOGIN, Account.class)
                .setParameter(LOGIN_PARAM, expectedAccount.getLogin())
                .getSingleResult();
        Account accountFromEmail = em.createNamedQuery(Account.GET_BY_EMAIL, Account.class)
                .setParameter(EMAIL_PARAM, expectedAccount.getEmail())
                .getSingleResult();
        Account accountFromTokenHash = em.createNamedQuery(Account.GET_BY_TOKEN_HASH, Account.class)
                .setParameter(HASH_PARAM, expectedToken.getTokenHash())
                .getSingleResult();
        assertThat(expectedAccount, allOf(is(accountFromAll),
                                          is(accountFromLogin),
                                          is(accountFromEmail),
                                          is(accountFromTokenHash)));

        List<Token> tokensFromAll = em.createNamedQuery(Token.GET_ALL, Token.class)
                .getResultList();
        Token tokenFromHash = em.createNamedQuery(Token.GET_BY_HASH, Token.class)
                .setParameter(HASH_PARAM, TOKEN_HASH_VALID)
                .getSingleResult();
        tokensFromAll.sort(Comparator.comparing(Token::getTokenHash));
        assertThat(tokensFromAll, is(tokensExpected));
        assertThat(tokenFromHash, is(expectedToken));

        transaction.begin();
        instant = Instant.now().plus(10, ChronoUnit.DAYS);
        em.createNamedQuery(Token.DELETE_EXPIRED_TO_DATE)
                .setParameter(DATE_PARAM, Date.from(instant)).executeUpdate();
        transaction.commit();
        tokensExpected.remove(expiringToken);
        tokensFromAll = em.createNamedQuery(Token.GET_ALL, Token.class)
                .getResultList();
        assertThat(tokensFromAll, is(tokensExpected));

        transaction.begin();
        em.createNamedQuery(Token.DELETE_BY_HASH)
                .setParameter(HASH_PARAM, expectedToken.getTokenHash()).executeUpdate();
        transaction.commit();
        tokensFromAll = em.createNamedQuery(Token.GET_ALL, Token.class)
                .getResultList();
        assertTrue(tokensFromAll.isEmpty());
    }

    @Test
    public void persistAnotherAccount() {
        Account account = new Account(0, LOGIN_INVALID, PASS_HASH_VALID, "new@email.com", new ArrayList<>(), roles);

        transaction.begin();
        em.persist(account);
        transaction.commit();
    }

    @Test(expected = RollbackException.class)
    public void persistLoginExists() {
        Account account = new Account(0, LOGIN_VALID, PASS_HASH_VALID, "new@email.com", new ArrayList<>(), roles);

        transaction.begin();
        em.persist(account);
        transaction.commit();
    }

    @Test(expected = RollbackException.class)
    public void persistEmailExists() {
        Account account = new Account(0, LOGIN_INVALID, PASS_HASH_VALID, EMAIL_VALID, new ArrayList<>(), roles);

        transaction.begin();
        em.persist(account);
        transaction.commit();
    }
}