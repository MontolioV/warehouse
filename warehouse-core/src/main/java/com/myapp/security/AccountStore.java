package com.myapp.security;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.myapp.security.Roles.Const.*;

@Stateless
public class AccountStore {
    private static final String[] PASS_MUST_HAVE_PATTERNS = {".*[a-z].*", ".*[A-Z].*", ".*[0-9].*"};

    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager em;
    @EJB
    private Encryptor encryptor;
    @Resource
    private SessionContext sessionContext;

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
        em.detach(account);
        return account;
    }

    public Optional<Account> getAccountByLogin(@NotBlank String login) {
        try {
            Account account = getAccountByLoginNotSafe(login);
            em.detach(account);
            return Optional.of(account);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    private Account getAccountByLoginNotSafe(String login) throws NoResultException {
        return em.createNamedQuery(Account.GET_BY_LOGIN, Account.class)
                .setParameter("login", login)
                .getSingleResult();
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
            Account account = em.createNamedQuery(Account.GET_BY_TOKEN_HASH, Account.class)
                    .setParameter("hash", tokenHash)
                    .getSingleResult();
            em.detach(account);
            return Optional.of(account);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    private Account findAccountByID(long id) throws NoResultException {
        Account result = em.find(Account.class, id);
        if (result != null) {
            return result;
        }
        throw new NoResultException();
    }

    @RolesAllowed(ADMIN)
    public List<Account> getAllAccounts() {
        List<Account> resultList = em.createNamedQuery(Account.GET_ALL, Account.class).getResultList();
        resultList.forEach(em::detach);
        return resultList;
    }

    @RolesAllowed({MODERATOR, ADMIN})
    public void changeAccountStatus(long accountID, boolean isActive) throws NoResultException{
        findAccountByID(accountID).setActive(isActive);
    }

    @RolesAllowed(ADMIN)
    public void changeAccountPassword(long accountID, @NotBlank String newPassword) throws UnsecurePasswordException {
        if (!isPasswordSecure(newPassword)) {
            throw new UnsecurePasswordException();
        }

        findAccountByID(accountID).setPassHash(encryptor.generate(newPassword));
    }

    @RolesAllowed(USER)
    public void changeSelfAccountPassword(@NotBlank String newPassword) throws UnsecurePasswordException {
        if (!isPasswordSecure(newPassword)) {
            throw new UnsecurePasswordException();
        }

        getAccountByLoginNotSafe(sessionContext.getCallerPrincipal().getName())
                .setPassHash(encryptor.generate(newPassword));
    }

    @RolesAllowed(ADMIN)
    public void changeAccountEmail(long accountID, @NotBlank String newEmail) {
        findAccountByID(accountID).setEmail(newEmail);
    }

    @RolesAllowed(USER)
    public void changeSelfAccountEmail(@NotBlank String newEmail) {
        getAccountByLoginNotSafe(sessionContext.getCallerPrincipal().getName()).setEmail(newEmail);
    }

    @RolesAllowed(ADMIN)
    public void addRoleToAccount(long accountID, @NotNull Roles role) {
        findAccountByID(accountID).getRoles().add(role);
    }

    @RolesAllowed(ADMIN)
    public void removeRoleFromAccount(long accountID, @NotNull Roles role) {
        findAccountByID(accountID).getRoles().remove(role);
    }

    @RolesAllowed(ADMIN)
    public void setNewRolesToAccount(long accountID, @NotNull Set<Roles> roles) {
        findAccountByID(accountID).setRoles(roles);
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

    public Encryptor getEncryptor() {
        return encryptor;
    }

    public void setEncryptor(Encryptor encryptor) {
        this.encryptor = encryptor;
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public SessionContext getSessionContext() {
        return sessionContext;
    }

    public void setSessionContext(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }
}
