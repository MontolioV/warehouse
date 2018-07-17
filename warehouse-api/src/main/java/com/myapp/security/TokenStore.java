package com.myapp.security;

import javax.validation.constraints.NotNull;
import java.time.temporal.ChronoUnit;

/**
 * <p>Created by MontolioV on 03.07.18.
 */
public interface TokenStore {


    Token createToken(@NotNull Account account, TokenType tokenType, int duration, ChronoUnit chronoUnit);

    Token findToken(String tokenHash);

    void removeToken(String tokenHash);

    int removeExpiredTokens();

    void removeAllRememberMeTokens(@NotNull Account account);
}
