package com.myapp.security;

import javax.mail.MessagingException;
import javax.validation.constraints.NotNull;

/**
 * <p>Created by MontolioV on 17.07.18.
 */
public interface AccountActivator {

    void prepareActivation(@NotNull Account account) throws MessagingException;
}
