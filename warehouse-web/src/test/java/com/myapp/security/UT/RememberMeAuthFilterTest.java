package com.myapp.security.UT;

import com.myapp.security.RememberMeAuthFilter;
import com.myapp.security.RememberMeAuthenticator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.verify;

/**
 * <p>Created by MontolioV on 04.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class RememberMeAuthFilterTest {
    @InjectMocks
    private RememberMeAuthFilter filter;
    @Mock
    private RememberMeAuthenticator authenticatorMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private HttpServletResponse responseMock;
    @Mock
    private FilterChain chainMock;

    @Test
    public void success() throws IOException, ServletException {
        filter.doFilter(requestMock, responseMock, chainMock);

        verify(authenticatorMock).cookieAuth(requestMock);
        verify(chainMock).doFilter(requestMock, responseMock);
    }
}