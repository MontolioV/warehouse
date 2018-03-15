package com.myapp.security;

import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 * <p>Created by MontolioV on 13.03.18.
 */
@Model
public class RegistrationController {
    @Inject
    private FacesContext facesContext;
    @EJB
    private AccountStore accountStore;
    private Account account = new Account();

    public String register() {
        try {
            accountStore.createAccount(account);
            facesContext.addMessage(null, new FacesMessage("Account has been successfully registered!"));
            return "index?faces-redirect=true";
        } catch (LoginExistsException e) {
            facesContext.addMessage("m_login", new FacesMessage("Login already exists!"));
        } catch (UnsecurePasswordException e) {
            facesContext.addMessage("m_password", new FacesMessage("Password is not secure!"));
        }
        return "";
    }

    public AccountStore getAccountStore() {
        return accountStore;
    }

    public void setAccountStore(AccountStore accountStore) {
        this.accountStore = accountStore;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
