package com.myapp.security;

import javax.mail.MessagingException;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.core.Response;

/**
 * <p>Created by MontolioV on 17.07.18.
 */
public interface AccountActivator {

    Response activate(String tokenHash);

    Response prepareActivation(@NotBlank String login) throws MessagingException;
}
