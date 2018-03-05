package com.myapp.security;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class AccountStore {
    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager em;
    @EJB
    private Encryptor encryptor;

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

        em.persist(account);
        return account;
    }

    public Account findAccountByLogin(String login) {
        return em.createNamedQuery(Account.GET_BY_LOGIN, Account.class)
                .setParameter("login", login)
                .getSingleResult();
    }

    public List<Account> getAllAccounts() {
        return em.createNamedQuery(Account.GET_ALL, Account.class).getResultList();
    }

    public Account changeAccountStatus(Account account, boolean isActive) {
        account.setActive(isActive);
        return em.merge(account);
    }

    public Account changeAccountPassword(Account account, String newPassword) throws UnsecurePasswordException {
        if (!isPasswordSecure(newPassword)) {
            throw new UnsecurePasswordException();
        }

        account.setPassHash(encryptor.generate(newPassword));
        return em.merge(account);
    }

    public Account changeAccountEmail(Account account, String newEmail) {
        account.setEmail(newEmail);
        return em.merge(account);
    }

    public Account addRoleToAccount(Account account, Roles role) {
        if (account.getRoles().contains(role)) {
            return account;
        }
        account.getRoles().add(role);
        return em.merge(account);
    }

    public Account removeRoleFromAccount(Account account, Roles role) {
        if (!account.getRoles().contains(role)) {
            return account;
        }
        account.getRoles().remove(role);
        return em.merge(account);
    }

    private boolean isPasswordSecure(String password) {
        boolean matchesPattern = password.matches("(\\p{Lower}+)&&(\\p{Upper}+)&&(\\d+)");
        if (password.length() > 6 && matchesPattern) {
            return true;
        }
        return false;
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
