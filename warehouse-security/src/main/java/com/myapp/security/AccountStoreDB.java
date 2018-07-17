package com.myapp.security;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
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
@Local(AccountStore.class)
public class AccountStoreDB implements AccountStore {
    private static final String[] PASS_MUST_HAVE_PATTERNS = {".*[a-z].*", ".*[A-Z].*", ".*[0-9].*"};

    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager em;
    @EJB
    private Encryptor encryptor;
    @EJB
    private AccountActivator accountActivator;
    @Resource
    private SessionContext sessionContext;

    @Override
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
        em.flush();
        em.detach(account);

        // TODO: 17.07.18 Handle exception
        try {
            accountActivator.prepareActivation(account);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return account;
    }

    @Override
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

    @Override
    public Optional<Account> getAccountByLoginAndPassword(@NotBlank String login, @NotBlank String password) {
        Optional<Account> optionalAccount = getAccountByLogin(login);
        if (optionalAccount.isPresent() && !encryptor.verify(password, optionalAccount.get().getPassHash())) {
            return Optional.empty();
        } else {
            return optionalAccount;
        }
    }

    @Override
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

    @Override
    @RolesAllowed(ADMIN)
    public List<Account> getAllAccounts() {
        List<Account> resultList = em.createNamedQuery(Account.GET_ALL, Account.class).getResultList();
        resultList.forEach(em::detach);
        return resultList;
    }

    @Override
    @RolesAllowed({MODERATOR, ADMIN})
    public void changeAccountStatus(long accountID, boolean isActive) throws NoResultException{
        findAccountByID(accountID).setActive(isActive);
    }

    @Override
    @RolesAllowed(ADMIN)
    public void changeAccountPassword(long accountID, @NotBlank String newPassword) throws UnsecurePasswordException {
        if (!isPasswordSecure(newPassword)) {
            throw new UnsecurePasswordException();
        }

        findAccountByID(accountID).setPassHash(encryptor.generate(newPassword));
    }

    @Override
    @RolesAllowed(USER)
    public void changeSelfAccountPassword(@NotBlank String newPassword) throws UnsecurePasswordException {
        if (!isPasswordSecure(newPassword)) {
            throw new UnsecurePasswordException();
        }

        getAccountByLoginNotSafe(sessionContext.getCallerPrincipal().getName())
                .setPassHash(encryptor.generate(newPassword));
    }

    @Override
    @RolesAllowed(ADMIN)
    public void changeAccountEmail(long accountID, @NotBlank String newEmail) {
        findAccountByID(accountID).setEmail(newEmail);
    }

    @Override
    @RolesAllowed(USER)
    public void changeSelfAccountEmail(@NotBlank String newEmail) {
        getAccountByLoginNotSafe(sessionContext.getCallerPrincipal().getName()).setEmail(newEmail);
    }

    @Override
    @RolesAllowed(ADMIN)
    public void addRoleToAccount(long accountID, @NotNull Roles role) {
        findAccountByID(accountID).getRoles().add(role);
    }

    @Override
    @RolesAllowed(ADMIN)
    public void removeRoleFromAccount(long accountID, @NotNull Roles role) {
        findAccountByID(accountID).getRoles().remove(role);
    }

    @Override
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
}
