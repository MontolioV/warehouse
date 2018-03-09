package com.myapp.security.UT;

import com.myapp.security.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 09.03.18.
 */
@Ignore
@RunWith(MockitoJUnitRunner.class)
public class TokenStoreTest {
    @InjectMocks
    private TokenStore tokenStore;
    @Mock
    private EntityManager emMock;
    @Mock
    private Encryptor encryptorMock;
    @Mock
    private Token tokenMock;
    @Mock
    private Account accountMock;
    private String hashString = "hash";

    @Before
    public void setUp() throws Exception {
        when(encryptorMock.generate(any(String.class))).thenReturn(hashString);
    }

    @Test
    public void createToken() {
        Token token = tokenStore.createToken(accountMock, any(TokenType.class), any(Date.class));

        verify(encryptorMock).generate(any(String.class));
        verify(accountMock).addToken(token);
        verify(emMock).merge(accountMock);
        assertThat(token.getTokenHash(), is(hashString));
    }

    @Test
    public void findToken() {
        Query queryMock = mock(Query.class);
        when(emMock.createNamedQuery(Token.DELETE_BY_HASH)).thenReturn(queryMock);
        when(queryMock.setParameter("hash", hashString)).thenReturn(queryMock);

    }

    @Test
    public void removeToken() {
        Query queryMock = mock(Query.class);
        when(emMock.createNamedQuery(Token.DELETE_BY_HASH)).thenReturn(queryMock);
        when(queryMock.setParameter("hash", hashString)).thenReturn(queryMock);

        tokenStore.removeToken(hashString);
        verify(queryMock.executeUpdate());
    }

    @Test
    public void removeExpiredTokens() {
        Query queryMock = mock(Query.class);
        when(emMock.createNamedQuery(Token.DELETE_EXPIRED_TO_DATE)).thenReturn(queryMock);
        when(queryMock.setParameter("date", any(Date.class))).thenReturn(queryMock);
        when(queryMock.executeUpdate()).thenReturn(123);

        int deleted = tokenStore.removeExpiredTokens();
        assertThat(deleted, is(123));
    }
}