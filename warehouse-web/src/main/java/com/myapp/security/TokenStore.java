package com.myapp.security;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TokenStore {
    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager entityManager;
    @EJB
    private Encryptor encryptor;

    public Token createToken() {

        return null;
    }

    public Token getTokenByHash(String tokenHash) {

        return null;
    }

    public void removeToken(String tokenHash) {

    }

    public int removeExpiredTokens() {

        return 0;
    }
}
