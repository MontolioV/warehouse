package com.myapp.utils.UT;

import com.myapp.utils.Hasher;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 11.05.18.
 */
public class HasherTest {

    @Test
    public void makeHash() throws IOException {
        byte[] bytes = {127, -128};

        MessageDigest mdMock = mock(MessageDigest.class);
        InputStream isMock = mock(InputStream.class);

        when(mdMock.digest()).thenReturn(bytes);
        when(isMock.read(any(byte[].class))).thenReturn((int) Math.pow(2, 20), 1, -1);

        Hasher hasher = new Hasher(mdMock);
        String hash = hasher.makeHash(isMock);

        verify(mdMock).update(any(byte[].class));
        verify(mdMock).update(any(byte[].class), eq(0), eq(1));
        int length = (int) (Math.pow(2, 20) + 1);
        assertThat(hash, is("7F80_" + length));
    }
}