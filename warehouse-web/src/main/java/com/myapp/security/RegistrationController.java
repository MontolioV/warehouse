package com.myapp.security;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;

/**
 * <p>Created by MontolioV on 13.03.18.
 */
@Model
public class RegistrationController {
    @Resource(lookup = "java:/strings/webAppAddress")
    private String webAppAddress;
    @Inject
    private FacesContext facesContext;
    @EJB
    private AccountStore accountStore;
    private Account account = new Account();
    private String password;
    private String passwordConfirm;

    public void registration() throws IOException {
        try {
            Account newAccount = accountStore.createAccount(this.account);
            facesContext.addMessage(null, new FacesMessage("Account has been successfully registered!"));
            String restSendEmailUrl = webAppAddress + "rs/activation/" + newAccount.getLogin() + "/send-email";
            facesContext.getExternalContext().redirect(restSendEmailUrl);
        } catch (LoginExistsException e) {
            facesContext.addMessage("reg_form:login", new FacesMessage("Login already exists!"));
        } catch (UnsecurePasswordException e) {
            facesContext.addMessage("reg_form:password", new FacesMessage("Password is not secure!"));
        }
    }

    public void passwordConfirmation() {
        if (password.equals(passwordConfirm)) {
            account.setPassHash(password);
        } else {
            facesContext.addMessage("reg_form:passwordConf", new FacesMessage("Password is not confirmed!"));
        }
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

    public FacesContext getFacesContext() {
        return facesContext;
    }

    public void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getWebAppAddress() {
        return webAppAddress;
    }

    public void setWebAppAddress(String webAppAddress) {
        this.webAppAddress = webAppAddress;
    }
}
