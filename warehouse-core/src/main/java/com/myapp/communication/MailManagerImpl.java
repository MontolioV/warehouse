package com.myapp.communication;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * <p>Created by MontolioV on 17.07.18.
 */
@Singleton
public class MailManagerImpl implements MailManager{
    @Resource(name = "java:/mail/warehouse")
    private Session session;

    @Override
    public void sendEmail(@Email String email, @NotBlank String subject, @NotBlank String htmlText) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject(subject);
        message.setText(htmlText, "UTF-8", "html");
        Transport.send(message);
    }
}
