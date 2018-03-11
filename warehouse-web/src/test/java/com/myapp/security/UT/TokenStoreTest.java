package com.myapp.security.UT;

import com.myapp.security.*;
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
@RunWith(MockitoJUnitRunner.class)
public class TokenStoreTest {
    @InjectMocks
    private TokenStore tokenStore;
    @Mock
    private EntityManager emMock;
    @Mock
    private Encryptor encryptorMock;
    @Mock
    private Account accountMock;
    private String hashString = "hash";

    @Test
    public void createToken() {
        when(encryptorMock.generate(any(String.class))).thenReturn(hashString);

        Token token = tokenStore.createToken(accountMock, TokenType.REMEMBER_ME, any(Date.class));

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
        verify(queryMock).executeUpdate();
    }

    @Test
    public void removeExpiredTokens() {
        Query queryMock = mock(Query.class);
        when(emMock.createNamedQuery(Token.DELETE_EXPIRED_TO_DATE)).thenReturn(queryMock);
        when(queryMock.setParameter(eq("date"), any())).thenReturn(queryMock);
        when(queryMock.executeUpdate()).thenReturn(123);

        int deleted = tokenStore.removeExpiredTokens();
        assertThat(deleted, is(123));
    }
}