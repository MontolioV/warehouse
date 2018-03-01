package com.myapp.security;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * <p>Created by MontolioV on 01.03.18.
 */
@Entity
@Access(value = AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Token.DELETE_BY_HASH, query = "delete from Token t where t.tokenHash=:hash"),
        @NamedQuery(name = Token.DELETE_OLDER_THAN, query = "delete from Token t where t.creation < :instant"),
})
@Table(indexes = {
        @Index(columnList = "TOKEN_HASH", unique = true)
})
public class Token implements Serializable {
    public static final String DELETE_BY_HASH = "Token.DELETE_BY_HASH";
    public static final String DELETE_OLDER_THAN = "Token.DELETE_OLDER_THAN";

    private long id;
    private String tokenHash;
    private TokenType tokenType;
    private Account account;
    private Instant creation;

    @NotNull
    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "TOKEN_TYPE")
    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    @NotNull
    @ManyToOne(optional = false)
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @NotNull
    @PastOrPresent
    @Column(nullable = false)
    public Instant getCreation() {
        return creation;
    }

    public void setCreation(Instant creation) {
        this.creation = creation;
    }

    @NotNull
    @Column(name = "TOKEN_HASH", nullable = false, unique = true)
    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;
        Token token = (Token) o;
        return id == token.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
