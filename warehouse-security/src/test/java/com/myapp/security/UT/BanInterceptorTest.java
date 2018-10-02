package com.myapp.security.UT;

import com.myapp.security.AccountStore;
import com.myapp.security.BanInterceptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.interceptor.InvocationContext;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BanInterceptorTest {
    @InjectMocks
    private BanInterceptor interceptor;
    @Mock
    private AccountStore asMock;
    @Mock
    private InvocationContext icMock;

    @Test
    public void passOnlyNotBanned_NotBanned() throws Exception {
        when(asMock.isActiveSelf()).thenReturn(true);
        interceptor.passOnlyNotBanned(icMock);
        verify(icMock).proceed();
    }

    @Test(expected = IllegalStateException.class)
    public void passOnlyNotBanned_Banned() throws Exception {
        when(asMock.isActiveSelf()).thenReturn(false);
        try {
            interceptor.passOnlyNotBanned(icMock);
        } catch (IllegalStateException e) {
            verify(icMock, never()).proceed();
            throw e;
        }
    }
}