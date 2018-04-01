package com.myapp.security;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.*;

/**
 * <p>Created by MontolioV on 01.03.18.
 */
@Entity
@Access(value = AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Account.GET_ALL, query = "select a from Account a"),
        @NamedQuery(name = Account.GET_BY_LOGIN, query = "select a from Account a where a.login = :login"),
        @NamedQuery(name = Account.GET_BY_TOKEN_HASH, query = "select a from Account a inner join Token t where t.tokenHash=:hash"),
})
@Table(indexes = {
        @Index(columnList = "LOGIN", unique = true)
})
@XmlRootElement
public class Account implements Serializable {
    private static final String PREFIX = "com.myapp.security.Account.";
    public static final String GET_ALL = PREFIX + "GET_ALL";
    public static final String GET_BY_LOGIN = PREFIX + "GET_BY_LOGIN";
    public static final String GET_BY_TOKEN_HASH = PREFIX + "GET_BY_TOKEN_HASH";

    private long id;
    private String login;
    private String passHash;
    private String email;
    private List<Token> tokens = new ArrayList<>();
    private Set<Roles> roles = new HashSet<>();
    private boolean active = false;

    public Account() {
    }

    public Account(long id, String login, String passHash, String email, List<Token> tokens, Set<Roles> roles) {
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
    //@Size(min = 128, max = 128)
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "ACCOUNT_ID")
    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void addToken(Token token) {
        tokens.add(token);
    }

    @NotNull
    @Size(min = 1)
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Set<Roles> getRoles() {
        return roles;
    }

    public void setRoles(Set<Roles> roles) {
        this.roles = roles;
    }

    public void addRole(Roles role) {
        roles.add(role);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return id == account.id &&
                active == account.active &&
                Objects.equals(login, account.login) &&
                Objects.equals(passHash, account.passHash) &&
                Objects.equals(email, account.email) &&
                Objects.equals(tokens, account.tokens) &&
                Objects.equals(roles, account.roles);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, login, passHash, email, tokens, roles, active);
    }
}
