package com.myapp.security;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.enterprise.credential.CallerOnlyCredential;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class CustomIdentityStore implements IdentityStore {
    @EJB
    private AccountStore accountStore;

    @Override
    public CredentialValidationResult validate(@NotNull Credential credential) {
        try {
            if (credential instanceof UsernamePasswordCredential) {
                UsernamePasswordCredential usernamePasswordCredential = (UsernamePasswordCredential) credential;
                String login = usernamePasswordCredential.getCaller();
                String password = usernamePasswordCredential.getPasswordAsString();

                return validate(accountStore.getAccountByLoginAndPassword(login, password)
                        .orElseThrow(AccountNotFoundException::new));

            } else if (credential instanceof CallerOnlyCredential) {
                CallerOnlyCredential callerOnlyCredential = (CallerOnlyCredential) credential;
                String login = callerOnlyCredential.getCaller();

                return validate(accountStore.getAccountByLogin(login)
                        .orElseThrow(AccountNotFoundException::new));
            }
        } catch (AccountNotFoundException e) {
            return CredentialValidationResult.INVALID_RESULT;
        }
        return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }

    private CredentialValidationResult validate(Account account) {
        if (account.isActive()) {
            Set<String> rolesSet = new HashSet<>();
            account.getRoles().forEach(rolesEnumList -> rolesSet.add(rolesEnumList.name()));
            return new CredentialValidationResult(account.getLogin(), rolesSet);
        }
        return CredentialValidationResult.INVALID_RESULT;
    }

    public AccountStore getAccountStore() {
        return accountStore;
    }

    public void setAccountStore(AccountStore accountStore) {
        this.accountStore = accountStore;
    }
}
