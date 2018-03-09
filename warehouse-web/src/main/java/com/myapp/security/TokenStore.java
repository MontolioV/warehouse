package com.myapp.security;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Stateless
public class TokenStore {
    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager entityManager;
    @EJB
    private Encryptor encryptor;

    public Token createToken(@NotNull Account account, TokenType tokenType, Date expiringDate) {
        return null;
    }

    public Token findToken(String tokenHash) {

        return null;
    }

    public void removeToken(String tokenHash) {

    }

    public int removeExpiredTokens() {

        return 0;
    }
}
