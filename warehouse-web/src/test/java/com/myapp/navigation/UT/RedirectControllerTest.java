package com.myapp.navigation.UT;

import com.myapp.navigation.RedirectController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.context.ExternalContext;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 03.05.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class RedirectControllerTest {
    @InjectMocks
    private RedirectController controller;
    @Mock
    private ExternalContext ecMock;

    @Before
    public void setUp() throws Exception {
        when(ecMock.getApplicationContextPath()).thenReturn("getApplicationContextPath");
    }

    @Test
    public void logoutRedirect() throws IOException {
        String s = ecMock.getApplicationContextPath() + "/logout";
        controller.logoutRedirect();
        verify(ecMock).redirect(s);
    }
}