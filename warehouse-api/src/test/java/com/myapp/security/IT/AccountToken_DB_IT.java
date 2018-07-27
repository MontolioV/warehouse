package com.myapp.security.IT;

import com.myapp.IT.AbstractITArquillianWithEM;
import com.myapp.security.Account;
import com.myapp.security.Roles;
import com.myapp.security.Token;
import com.myapp.security.TokenType;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static com.myapp.security.Account.EMAIL_PARAM;
import static com.myapp.security.Account.LOGIN_PARAM;
import static com.myapp.security.Token.DATE_PARAM;
import static com.myapp.security.Token.HASH_PARAM;
import static com.myapp.security.TokenType.EMAIL_VERIFICATION;
import static com.myapp.security.TokenType.REMEMBER_ME;
import static com.myapp.utils.TestSecurityConstants.*;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AccountToken_DB_IT extends AbstractITArquillianWithEM {
    public static final String HASH = "someHash";

    @Deployment
    public static WebArchive createDeployment() {
        return AbstractITArquillianWithEM.createDeployment()
                .addClasses(Account.class, Token.class, Roles.class, TokenType.class);
    }

    @Test
    @InSequence(0)
    public void persist() throws Exception {
        HashSet<Roles> roles = new HashSet<>();
        roles.add(Roles.ADMIN);
        Account expectedAccount = new Account(0, LOGIN_VALID, PASS_HASH_VALID, EMAIL_VALID, new ArrayList<>(), roles);

        Instant instant = Instant.now().plus(14, ChronoUnit.DAYS);
        expectedAccount.addToken(new Token(0, TOKEN_HASH_VALID, REMEMBER_ME, new Date(), Date.from(instant)));
        expectedAccount.addToken(new Token(0, HASH, REMEMBER_ME, new Date(), Date.from(instant)));

        instant = Instant.now().plus(1, ChronoUnit.DAYS);
        Token expiringToken = new Token(0, TOKEN_HASH_INVALID, REMEMBER_ME, new Date(), Date.from(instant));

        persistAllowed(expectedAccount, expiringToken);
    }

    @Test
    @InSequence(1)
    public void persistFail() throws Exception {
        String newEmail = "a" + EMAIL_VALID;
        ArrayList<Object> objects = new ArrayList<>();

        objects.add(new Account(0, null, PASS_HASH_VALID, newEmail));
        objects.add(new Account(0, "", PASS_HASH_VALID, newEmail));
        objects.add(new Account(0, "1", PASS_HASH_VALID, newEmail));
        objects.add(new Account(0, repeat("1", 31), PASS_HASH_VALID, newEmail));
        objects.add(new Account(0, LOGIN_VALID, PASS_HASH_VALID, newEmail + "1"));
        objects.add(new Account(0, LOGIN_INVALID, null, newEmail));
        objects.add(new Account(0, LOGIN_INVALID, PASS_HASH_VALID, null));
        objects.add(new Account(0, LOGIN_INVALID, PASS_HASH_VALID, ""));
        objects.add(new Account(0, LOGIN_INVALID, PASS_HASH_VALID, repeat("1", 320).concat(newEmail)));
        objects.add(new Account(0, LOGIN_INVALID, PASS_HASH_VALID, newEmail));
        objects.add(new Account(0, LOGIN_INVALID, PASS_HASH_VALID, EMAIL_VALID));
        objects.add(new Account(0, LOGIN_INVALID, PASS_HASH_VALID, "asd"));
        objects.add(new Account(0, LOGIN_INVALID, PASS_HASH_VALID, "@asd"));
        objects.add(new Account(0, LOGIN_INVALID, PASS_HASH_VALID, "asd@"));
        objects.add(new Account(0, LOGIN_INVALID, PASS_HASH_VALID, "asd.asd.asd"));
        objects.add(new Account(0, LOGIN_INVALID, PASS_HASH_VALID, newEmail, null, new HashSet<>()));
        objects.add(new Account(0, LOGIN_INVALID, PASS_HASH_VALID, newEmail, new ArrayList<>(), null));

        Instant instant = Instant.now().plus(14, ChronoUnit.DAYS);
        objects.add(new Token(0, TOKEN_HASH_INVALID, null, new Date(), Date.from(instant)));
        objects.add(new Token(0, TOKEN_HASH_INVALID, REMEMBER_ME, null, Date.from(instant)));
        objects.add(new Token(0, TOKEN_HASH_INVALID, REMEMBER_ME, Date.from(instant), Date.from(instant)));
        objects.add(new Token(0, TOKEN_HASH_INVALID, REMEMBER_ME, new Date(), null));
        objects.add(new Token(0, TOKEN_HASH_INVALID, REMEMBER_ME, new Date(), new Date()));
        objects.add(new Token(0, null, REMEMBER_ME, new Date(), Date.from(instant)));
        objects.add(new Token(0, "", REMEMBER_ME, new Date(), Date.from(instant)));
        objects.add(new Token(0, TOKEN_HASH_VALID, EMAIL_VERIFICATION, new Date(), Date.from(instant)));
        objects.add(new Token(0, TOKEN_HASH_INVALID, REMEMBER_ME, new Date(), Date.from(instant)));
        objects.add(new Token(0, TOKEN_HASH_INVALID, REMEMBER_ME, new Date(), Date.from(instant)));

        int persisted = persistNotAllowed(objects);
        assertThat(persisted, is(0));
    }

    @Test
    @InSequence(2)
    public void queryTests() {
        Account accountFromAll = em.createNamedQuery(Account.GET_ALL, Account.class)
                .getSingleResult();
        Account accountFromLogin = em.createNamedQuery(Account.GET_BY_LOGIN, Account.class)
                .setParameter(LOGIN_PARAM, LOGIN_VALID)
                .getSingleResult();
        Account accountFromEmail = em.createNamedQuery(Account.GET_BY_EMAIL, Account.class)
                .setParameter(EMAIL_PARAM, EMAIL_VALID)
                .getSingleResult();
        Account accountFromTokenHash = em.createNamedQuery(Account.GET_BY_TOKEN_HASH, Account.class)
                .setParameter(HASH_PARAM, TOKEN_HASH_VALID)
                .getSingleResult();
        assertThat(accountFromAll.getLogin(), is(LOGIN_VALID));
        assertThat(accountFromAll.getEmail(), is(EMAIL_VALID));
        assertThat(accountFromAll.getPassHash(), is(PASS_HASH_VALID));
        assertThat(accountFromAll.getRoles().size(), is(1));
        assertThat(accountFromAll.getRoles().iterator().next(), is(Roles.ADMIN));
        assertThat(accountFromAll.getTokens().size(), is(2));
        assertThat(accountFromAll.getTokens().iterator().next().getTokenHash(), anyOf(is(TOKEN_HASH_VALID), is(HASH)));
        assertThat(accountFromAll, allOf(sameInstance(accountFromLogin),
                                         sameInstance(accountFromEmail),
                                         sameInstance(accountFromTokenHash)));

        List<Token> tokensFromAll = em.createNamedQuery(Token.GET_ALL, Token.class)
                .getResultList();
        assertThat(tokensFromAll.size(), is(3));
        assertThat(tokensFromAll.get(0), allOf(not(tokensFromAll.get(1)), not(tokensFromAll.get(2))));
        Token tokenFromHash = em.createNamedQuery(Token.GET_BY_HASH, Token.class)
                .setParameter(HASH_PARAM, TOKEN_HASH_VALID)
                .getSingleResult();
        assertThat(tokenFromHash.getTokenHash(), is(TOKEN_HASH_VALID));

        Instant instant = Instant.now().plus(10, ChronoUnit.DAYS);
        em.createNamedQuery(Token.DELETE_EXPIRED_TO_DATE)
                .setParameter(DATE_PARAM, Date.from(instant)).executeUpdate();
        tokensFromAll = em.createNamedQuery(Token.GET_ALL, Token.class)
                .getResultList();
        assertThat(tokensFromAll.size(), is(2));

        em.createNamedQuery(Token.DELETE_BY_HASH)
                .setParameter(HASH_PARAM, TOKEN_HASH_VALID).executeUpdate();
        Token token = em.createNamedQuery(Token.GET_ALL, Token.class)
                .getSingleResult();
        assertThat(token.getTokenHash(), is(HASH));

    }

    @Test
    @InSequence(3)
    public void updateAccount() {
        Account accountFromLogin = em.createNamedQuery(Account.GET_BY_LOGIN, Account.class)
                .setParameter(LOGIN_PARAM, LOGIN_VALID)
                .getSingleResult();
        em.detach(accountFromLogin);
        em.find(Account.class, accountFromLogin.getId()).setActive(true);
    }

    @Test
    @InSequence(4)
    public void clearDB() {
        Account accountFromLogin = em.createNamedQuery(Account.GET_BY_LOGIN, Account.class)
                .setParameter(LOGIN_PARAM, LOGIN_VALID)
                .getSingleResult();
        assertTrue(accountFromLogin.isActive());

        em.remove(em.createNamedQuery(Account.GET_ALL).getSingleResult());
        assertTrue(em.createNamedQuery(Account.GET_ALL).getResultList().isEmpty());
        assertTrue(em.createNamedQuery(Token.GET_ALL).getResultList().isEmpty());
    }
}