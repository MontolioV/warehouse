package com.myapp.security;

import javax.persistence.NoResultException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * <p>Created by MontolioV on 03.07.18.
 */
public interface AccountStore {

    Account createAccount(@NotNull Account account) throws LoginExistsException, UnsecurePasswordException;

    Optional<Account> getAccountByLogin(@NotBlank String login);

    Optional<Account> getAccountByLoginAndPassword(@NotBlank String login, @NotBlank String password);

    Optional<Account> getAccountByTokenHash(@NotBlank String tokenHash);

    Optional<Account> getAccountByEmail(@NotBlank String email);

    List<Account> getAllAccounts();

    void activateAccount(@NotBlank String tokenHash);

    boolean isActiveSelf();

    void changeAccountStatus(long accountID, boolean isActive) throws NoResultException;

    void changeAccountPassword(long accountID, @NotBlank String newPassword) throws UnsecurePasswordException;

    void changeSelfAccountPassword(@NotBlank String newPassword) throws UnsecurePasswordException;

    void changeAccountEmail(long accountID, @NotBlank String newEmail);

    void changeSelfAccountEmail(@NotBlank String newEmail);

    void addRoleToAccount(long accountID, @NotNull Roles role);

    void removeRoleFromAccount(long accountID, @NotNull Roles role);

    void setNewRolesToAccount(long accountID, @NotNull Set<Roles> roles);
}
