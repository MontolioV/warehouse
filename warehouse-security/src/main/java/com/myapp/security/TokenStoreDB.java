package com.myapp.security;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.myapp.security.Roles.Const.USER;
import static java.util.UUID.randomUUID;

@Stateless
@Local(TokenStore.class)
public class TokenStoreDB implements TokenStore{
    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager em;
    @EJB
    private Encryptor encryptor;

    @Override
    public Token createToken(@NotNull Account account, TokenType tokenType, int duration, ChronoUnit chronoUnit) {
        String uuid = randomUUID().toString();
        String hash = encryptor.generate(uuid);
        Token newToken = new Token();
        newToken.setTokenHash(hash);
        newToken.setTokenType(tokenType);
        newToken.setCreationDate(new Date());

        Date expiringDate = Date.from(Instant.now().plus(duration, chronoUnit));
        newToken.setExpiredDate(expiringDate);

        account.addToken(newToken);
        em.merge(account);

        return newToken;
    }

    @Override
    public Token findToken(String tokenHash) {
        return em.createNamedQuery(Token.GET_BY_HASH, Token.class)
                .setParameter("hash", tokenHash)
                .getSingleResult();
    }

    @Override
    @RolesAllowed(USER)
    public void removeToken(String tokenHash) {
        em.createNamedQuery(Token.DELETE_BY_HASH)
                .setParameter("hash", tokenHash)
                .executeUpdate();
    }

    @Override
    public int removeExpiredTokens() {
        return em.createNamedQuery(Token.DELETE_EXPIRED_TO_DATE)
                .setParameter("date", new Date())
                .executeUpdate();
    }

    @Override
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
}
