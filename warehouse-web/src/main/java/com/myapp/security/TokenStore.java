package com.myapp.security;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.Date;

import static com.sun.xml.ws.security.impl.policy.PolicyUtil.randomUUID;

@Stateless
public class TokenStore {
    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager em;
    @EJB
    private Encryptor encryptor;

    public Token createToken(@NotNull Account account, TokenType tokenType, Date expiringDate) {
        String uuid = randomUUID();
        String hash = encryptor.generate(uuid);
        Token newToken = new Token();
        newToken.setTokenHash(hash);
        newToken.setTokenType(tokenType);
        newToken.setCreationDate(new Date());
        newToken.setExpiredDate(expiringDate);

        account.addToken(newToken);
        em.merge(account);

        return newToken;
    }

    public Token findToken(String tokenHash) {
        return em.createNamedQuery(Token.GET_BY_HASH, Token.class)
                .setParameter("hash", tokenHash)
                .getSingleResult();
    }

    public void removeToken(String tokenHash) {
        em.createNamedQuery(Token.DELETE_BY_HASH)
                .setParameter("hash", tokenHash)
                .executeUpdate();
    }

    public int removeExpiredTokens() {
        return em.createNamedQuery(Token.DELETE_EXPIRED_TO_DATE)
                .setParameter("date", new Date())
                .executeUpdate();
    }
}
