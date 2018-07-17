package com.myapp.communication;

import javax.mail.MessagingException;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * <p>Created by MontolioV on 17.07.18.
 */
public interface MailManager {
    void sendEmail(@Email String email, @NotBlank String subject, @NotBlank String htmlText) throws MessagingException;
}
