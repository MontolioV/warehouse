package com.myapp.security;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

@Singleton
public class TokenScheduler {
    @EJB
    private TokenStore tokenStore;

    @Schedule(hour = "*", persistent = false)
    public void hourly() {
        tokenStore.removeExpiredTokens();
    }

    public TokenStore getTokenStore() {
        return tokenStore;
    }

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }
}
