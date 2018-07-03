package com.myapp.security.UT;

import com.myapp.security.*;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.myapp.utils.TestSecurityConstants.TOKEN_HASH_VALID;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 09.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class TokenStoreDBTest implements CommonChecks {
    @InjectMocks
    private TokenStoreDB tokenStoreDB;
    @Mock
    private EntityManager emMock;
    @Mock
    private Encryptor encryptorMock;
    @Mock
    private Account accountMock;
    private List<Token> tokens;
    private Token rmToken;
    private Token otherToken;

    @Before
    public void setUp() throws Exception {
        tokens = new ArrayList<>();
        rmToken = mock(Token.class);
        otherToken = mock(Token.class);
        tokens.add(rmToken);
        tokens.add(otherToken);

        when(rmToken.getTokenType()).thenReturn(TokenType.REMEMBER_ME);
        when(otherToken.getTokenType()).thenReturn(null);
        when(accountMock.getTokens()).thenReturn(tokens);

    }

    @Test
    public void createToken() {
        when(encryptorMock.generate(any(String.class))).thenReturn(TOKEN_HASH_VALID);

        Token token = tokenStoreDB.createToken(accountMock, TokenType.REMEMBER_ME, new Date());

        verify(encryptorMock).generate(any(String.class));
        verify(accountMock).addToken(token);
        verify(emMock).merge(accountMock);
        assertThat(token.getTokenHash(), CoreMatchers.is(TOKEN_HASH_VALID));
    }

    @Test
    public void findToken() {
        Query queryMock = mock(Query.class);
        when(emMock.createNamedQuery(Token.DELETE_BY_HASH)).thenReturn(queryMock);
        when(queryMock.setParameter("hash", TOKEN_HASH_VALID)).thenReturn(queryMock);

    }

    @Test
    public void removeToken() {
        Query queryMock = mock(Query.class);
        when(emMock.createNamedQuery(Token.DELETE_BY_HASH)).thenReturn(queryMock);
        when(queryMock.setParameter("hash", TOKEN_HASH_VALID)).thenReturn(queryMock);

        tokenStoreDB.removeToken(TOKEN_HASH_VALID);
        verify(queryMock).executeUpdate();
    }

    @Test
    public void removeExpiredTokens() {
        Query queryMock = mock(Query.class);
        when(emMock.createNamedQuery(Token.DELETE_EXPIRED_TO_DATE)).thenReturn(queryMock);
        when(queryMock.setParameter(eq("date"), any())).thenReturn(queryMock);
        when(queryMock.executeUpdate()).thenReturn(123);

        int deleted = tokenStoreDB.removeExpiredTokens();
        assertThat(deleted, is(123));
    }

    @Test
    public void removeRememberMeTokens() {
        tokenStoreDB.removeAllRememberMeTokens(accountMock);
        verify(emMock).merge(accountMock);
        assertThat(tokens.size(), is(1));
        assertThat(tokens.contains(rmToken), is(false));
        assertThat(tokens.contains(otherToken), is(true));

    }
}