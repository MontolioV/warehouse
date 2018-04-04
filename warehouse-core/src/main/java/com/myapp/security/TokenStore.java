package com.myapp.security;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.myapp.security.Roles.Const.USER;
import static java.util.UUID.randomUUID;

@Stateless
public class TokenStore {
    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager em;
    @EJB
    private Encryptor encryptor;

    public Token createToken(@NotNull Account account, TokenType tokenType, Date expiringDate) {
        String uuid = randomUUID().toString();
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

    @RolesAllowed(USER)
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


    @RolesAllowed(USER)
    public void removeAllRememberMeTokens(@NotNull Account account) {
        List<Token> tokensToRemove = account.getTokens().stream()
                .filter(token -> token.getTokenType() != null && token.getTokenType().equals(TokenType.REMEMBER_ME))
                .collect(Collectors.toList());
        if (tokensToRemove.isEmpty()) {
            return;
        }

        account.getTokens().removeAll(tokensToRemove);
        em.merge(account);
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public Encryptor getEncryptor() {
        return encryptor;
    }

    public void setEncryptor(Encryptor encryptor) {
        this.encryptor = encryptor;
    }
}
