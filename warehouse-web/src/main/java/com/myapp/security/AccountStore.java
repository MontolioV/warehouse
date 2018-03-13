package com.myapp.security;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static com.myapp.security.Roles.Const.*;

@Stateless
public class AccountStore {
    private static final String[] PASS_MUST_HAVE_PATTERNS = {".*[a-z].*", ".*[A-Z].*", ".*[0-9].*"};

    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager em;
    @EJB
    private Encryptor encryptor;

    public Account createAccount(@NotNull Account account) throws LoginExistsException, UnsecurePasswordException {
        List<Account> existing = em.createNamedQuery(Account.GET_BY_LOGIN, Account.class)
                .setParameter("login", account.getLogin())
                .getResultList();
        if (!existing.isEmpty()) {
            throw new LoginExistsException();
        }
        if (!isPasswordSecure(account.getPassHash())) {
            throw new UnsecurePasswordException();
        }

        String encryptedPass = encryptor.generate(account.getPassHash());
        account.setPassHash(encryptedPass);

        account.addRole(Roles.USER);

        em.persist(account);
        return account;
    }

    public Optional<Account> getAccountByLogin(@NotBlank String login) {
        try {
            return Optional.of(em.createNamedQuery(Account.GET_BY_LOGIN, Account.class)
                    .setParameter("login", login)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Account> getAccountByLoginAndPassword(@NotBlank String login, @NotBlank String password) {
        Optional<Account> optionalAccount = getAccountByLogin(login);
        if (optionalAccount.isPresent() && !encryptor.verify(password, optionalAccount.get().getPassHash())) {
            return Optional.empty();
        } else {
            return optionalAccount;
        }
    }

    public Optional<Account> getAccountByTokenHash(@NotBlank String tokenHash) {
        try {
            return Optional.of(em.createNamedQuery(Account.GET_BY_TOKEN_HASH, Account.class)
                    .setParameter("hash", tokenHash)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @RolesAllowed(ADMIN)
    public List<Account> getAllAccounts() {
        return em.createNamedQuery(Account.GET_ALL, Account.class).getResultList();
    }

    @RolesAllowed(MODERATOR)
    public Account changeAccountStatus(@NotNull Account account, boolean isActive) {

        account.setActive(isActive);
        return em.merge(account);
    }

    @RolesAllowed(USER)
    public Account changeAccountPassword(@NotNull Account account, @NotBlank String newPassword) throws UnsecurePasswordException {
        if (!isPasswordSecure(newPassword)) {
            throw new UnsecurePasswordException();
        }

        account.setPassHash(encryptor.generate(newPassword));
        return em.merge(account);
    }

    @RolesAllowed(USER)
    public Account changeAccountEmail(@NotNull Account account, @NotBlank String newEmail) {
        account.setEmail(newEmail);
        return em.merge(account);
    }

    @RolesAllowed(ADMIN)
    public Account addRoleToAccount(@NotNull Account account, @NotNull Roles role) {
        if (account.getRoles().contains(role)) {
            return account;
        }
        account.getRoles().add(role);
        return em.merge(account);
    }

    @RolesAllowed(ADMIN)
    public Account removeRoleFromAccount(@NotNull Account account, Roles role) {
        if (!account.getRoles().contains(role)) {
            return account;
        }
        account.getRoles().remove(role);
        return em.merge(account);
    }

    private boolean isPasswordSecure(String password) {
        if (password == null || password.length() < 7) {
            return false;
        } else {
            for (String passMustHavePattern : PASS_MUST_HAVE_PATTERNS) {
                if (!password.matches(passMustHavePattern)) {
                    return false;
                }
            }
        }
        return true;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.em = entityManager;
    }

    public Encryptor getEncryptor() {
        return encryptor;
    }

    public void setEncryptor(Encryptor encryptor) {
        this.encryptor = encryptor;
    }
}
