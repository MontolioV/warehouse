package com.myapp.administration;

import com.myapp.security.Account;
import com.myapp.security.AccountStore;
import com.myapp.security.Roles;

import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.*;

/**
 * <p>Created by MontolioV on 06.04.18.
 */
@Model
public class UserManagementController {
    @Inject
    private FacesContext facesContext;
    @EJB
    private AccountStore accountStore;
    private List<Account> accountList;
    private Account singleAccount;
    private String login;
    private boolean active;
    private Set<Roles> roles;

    public void fetchAccounts() {
        accountList = accountStore.getAllAccounts();
    }

    public void fetchSingleAccount() {
        Optional<Account> optional = accountStore.getAccountByLogin(login);
        if (optional.isPresent()) {
            singleAccount = optional.get();
            accountList = new ArrayList<>();
            accountList.add(singleAccount);
            active = singleAccount.isActive();
            roles = singleAccount.getRoles();
        } else {
            facesContext.addMessage("usersListForm:login", new FacesMessage("User doesn't exist!"));
        }
    }

    public void updateAccount() {
        accountStore.changeAccountStatus(singleAccount, active);
        accountStore.setNewRolesToAccount(singleAccount, new HashSet<>(roles));
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    public AccountStore getAccountStore() {
        return accountStore;
    }

    public void setAccountStore(AccountStore accountStore) {
        this.accountStore = accountStore;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Account getSingleAccount() {
        return singleAccount;
    }

    public void setSingleAccount(Account singleAccount) {
        this.singleAccount = singleAccount;
    }

    public FacesContext getFacesContext() {
        return facesContext;
    }

    public void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Roles> getRoles() {
        return roles;
    }

    public void setRoles(Set<Roles> roles) {
        this.roles = roles;
    }
}
