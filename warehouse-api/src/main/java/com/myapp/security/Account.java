package com.myapp.security;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>Created by MontolioV on 01.03.18.
 */
@Entity
@Access(value = AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Account.GET_ALL, query = "select a from Account a"),
        @NamedQuery(name = Account.GET_BY_LOGIN, query = "select a from Account a where a.login = :login"),
})
@Table(indexes = {
        @Index(columnList = "LOGIN", unique = true)
})
public class Account implements Serializable {
    private static final String PREFIX = "com.myapp.security.Account.";
    public static final String GET_ALL = PREFIX + "GET_ALL";
    public static final String GET_BY_LOGIN = PREFIX + "GET_BY_LOGIN";

    private long id;
    private String login;
    private String passHash;
    private String email;
    private List<Token> tokens = new ArrayList<>();
    private List<Roles> roles = new ArrayList<>();

    public Account() {
    }

    public Account(long id, String login, String passHash, String email, List<Token> tokens, List<Roles> roles) {
        this.id = id;
        this.login = login;
        this.passHash = passHash;
        this.email = email;
        this.tokens = tokens;
        this.roles = roles;
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
    @Size(min = 1, max = 30)
    @Column(unique = true)
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @NotNull
    public String getPassHash() {
        return passHash;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }

    @Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "account")
    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    @NotNull
    @Size(min = 1)
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public List<Roles> getRoles() {
        return roles;
    }

    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }

    public void addToken(Token token) {
        tokens.add(token);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return id == account.id &&
                Objects.equals(login, account.login) &&
                Objects.equals(passHash, account.passHash) &&
                Objects.equals(email, account.email) &&
                Objects.equals(tokens, account.tokens) &&
                Objects.equals(roles, account.roles);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, login, passHash, email, tokens, roles);
    }
}
