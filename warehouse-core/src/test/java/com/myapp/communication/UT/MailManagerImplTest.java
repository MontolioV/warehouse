package com.myapp.communication.UT;

import com.myapp.communication.MailManagerImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * <p>Created by MontolioV on 17.07.18.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Transport.class, Session.class})
public class MailManagerImplTest {
    @InjectMocks
    private MailManagerImpl mailManager;
    @Mock
    private Session sessionMock;
    private String email = "email@email.com";
    private String title = "title";
    private String body = "body";

    @Test
    public void sendEmail() throws MessagingException, IOException {
        mockStatic(Transport.class);
        mockStatic(Session.class);
        when(sessionMock.getProperties()).thenReturn(new Properties());

        mailManager.sendEmail(email, title, body);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verifyStatic(Transport.class);
        Transport.send(captor.capture());
        Message messageCaptured = captor.getValue();
        assertThat(messageCaptured.getAllRecipients().length, is(1));
        assertThat(messageCaptured.getAllRecipients()[0].toString(), is(email));
        assertThat(messageCaptured.getSubject(), is(title));
        assertThat((String) messageCaptured.getContent(), is(body));
        assertThat(messageCaptured.getDataHandler().getContentType(), is("text/html; charset=UTF-8"));
    }
}