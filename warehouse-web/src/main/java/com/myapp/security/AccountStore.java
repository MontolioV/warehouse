package com.myapp.security;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static com.myapp.security.Roles.Const.*;

public class AccountStore {
    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager em;
    @EJB
    private Encryptor encryptor;
    private String[] passMustHavePatterns = {".*[a-z].*", ".*[A-Z].*", ".*[0-9].*"};

    public Account createAccount(Account account) throws LoginExistsException, UnsecurePasswordException {
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

    public Optional<Account> findAccountByLogin(String login) {
        try {
            return Optional.of(em.createNamedQuery(Account.GET_BY_LOGIN, Account.class)
                    .setParameter("login", login)
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
    public Account changeAccountStatus(Account account, boolean isActive) {

        account.setActive(isActive);
        return em.merge(account);
    }

    @RolesAllowed(USER)
    public Account changeAccountPassword(Account account, String newPassword) throws UnsecurePasswordException {
        if (!isPasswordSecure(newPassword)) {
            throw new UnsecurePasswordException();
        }

        account.setPassHash(encryptor.generate(newPassword));
        return em.merge(account);
    }

    @RolesAllowed(USER)
    public Account changeAccountEmail(Account account, String newEmail) {
        account.setEmail(newEmail);
        return em.merge(account);
    }

    @RolesAllowed(ADMIN)
    public Account addRoleToAccount(Account account, Roles role) {
        if (account.getRoles().contains(role)) {
            return account;
        }
        account.getRoles().add(role);
        return em.merge(account);
    }

    @RolesAllowed(ADMIN)
    public Account removeRoleFromAccount(Account account, Roles role) {
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
            for (String passMustHavePattern : passMustHavePatterns) {
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
