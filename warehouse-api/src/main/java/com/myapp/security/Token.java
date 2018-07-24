package com.myapp.security;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import static com.myapp.security.Token.*;

/**
 * <p>Created by MontolioV on 01.03.18.
 */
@Entity
@Access(value = AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = GET_ALL, query = "select t from Token t"),
        @NamedQuery(name = GET_BY_HASH, query = "select t from Token t where t.tokenHash=:" + HASH_PARAM),
        @NamedQuery(name = DELETE_BY_HASH, query = "delete from Token t where t.tokenHash=:" + HASH_PARAM),
        @NamedQuery(name = DELETE_EXPIRED_TO_DATE, query = "delete from Token t where t.expiredDate < :" + DATE_PARAM),
})
@Table(indexes = {
        @Index(columnList = "TOKEN_HASH", unique = true)
})
@XmlRootElement
public class Token implements Serializable {
    private static final String PREFIX = "com.myapp.security.Token.";
    public static final String GET_ALL = PREFIX + "GET_ALL";
    public static final String GET_BY_HASH = PREFIX + "GET_BY_HASH";
    public static final String DELETE_BY_HASH = PREFIX + "DELETE_BY_HASH";
    public static final String DELETE_EXPIRED_TO_DATE = PREFIX + "DELETE_EXPIRED_TO_DATE";
    public static final String HASH_PARAM = "HASH_PARAM";
    public static final String DATE_PARAM = "DATE_PARAM";

    private long id;
    private String tokenHash;
    private TokenType tokenType;
    private Date creationDate;
    private Date expiredDate;

    public Token() {
    }

    public Token(long id, String tokenHash, TokenType tokenType, Date creationDate, Date expiredDate) {
        this.id = id;
        this.tokenHash = tokenHash;
        this.tokenType = tokenType;
        this.creationDate = creationDate;
        this.expiredDate = expiredDate;
    }

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
    @PastOrPresent
    @Column(nullable = false)
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @NotNull
    @Future
    @Column(nullable = false)
    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    @NotBlank
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
        return id == token.id &&
                Objects.equals(tokenHash, token.tokenHash) &&
                tokenType == token.tokenType &&
                Objects.equals(creationDate, token.creationDate);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, tokenHash, tokenType, creationDate);
    }
}
