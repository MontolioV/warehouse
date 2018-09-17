package com.myapp.utils.UT;

import com.myapp.utils.FacesMessenger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import static javax.faces.application.FacesMessage.SEVERITY_INFO;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * <p>Created by MontolioV on 17.09.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class FacesMessengerTest {
    @InjectMocks
    private FacesMessenger messenger;
    @Mock
    private FacesContext fcMock;
    @Captor
    private ArgumentCaptor<FacesMessage> captor;
    private String message = "message";

    @Test
    public void addInfoMessage() {
        messenger.addInfoMessage(message);

        verify(fcMock).addMessage(eq(null), captor.capture());
        FacesMessage value = captor.getValue();
        assertThat(value.getSeverity(), is(SEVERITY_INFO));
        assertThat(value.getSummary(), is(message));
    }
}