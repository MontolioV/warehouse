package com.myapp.security.UT;

import com.myapp.security.TokenScheduler;
import com.myapp.security.TokenStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TokenSchedulerTest {
    @InjectMocks
    private TokenScheduler tokenScheduler;
    @Mock
    private TokenStore tokenStore;

    @Test
    public void hourly() {
        tokenScheduler.hourly();
        verify(tokenStore).removeExpiredTokens();
    }
}