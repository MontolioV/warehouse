package com.myapp.administration;

import com.myapp.security.Account;
import com.myapp.security.AccountStore;
import com.myapp.security.UnsecurePasswordException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@Model
public class PersonalCabinetController {
    @Inject
    private FacesContext facesContext;
    @EJB
    private AccountStore accountStore;
    private Account selfAccount;
    private String newPassword;
    private String email;
    private UIComponent newPasswordInput;

    @PostConstruct
    public void init() {
        fetchSelfAccount();
    }

    public void fetchSelfAccount() {
        selfAccount = accountStore.getAccountByLogin(facesContext.getExternalContext().getUserPrincipal().getName()).get();
    }

    public void changePassword() {
        try {
            accountStore.changeSelfAccountPassword(newPassword);
            facesContext.addMessage(null, new FacesMessage("Password has been changed."));
            fetchSelfAccount();
        } catch (UnsecurePasswordException e) {
            facesContext.addMessage(newPasswordInput.getClientId(), new FacesMessage(FacesMessage.SEVERITY_WARN, "Password is not secure!", null));
        }
    }

    public void changeEmail() {
        accountStore.changeSelfAccountEmail(email);
        facesContext.addMessage(null, new FacesMessage("Email has been changed."));
        fetchSelfAccount();
    }

    public AccountStore getAccountStore() {
        return accountStore;
    }

    public void setAccountStore(AccountStore accountStore) {
        this.accountStore = accountStore;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public FacesContext getFacesContext() {
        return facesContext;
    }

    public void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
    }

    public Account getSelfAccount() {
        return selfAccount;
    }

    public void setSelfAccount(Account selfAccount) {
        this.selfAccount = selfAccount;
    }

    public UIComponent getNewPasswordInput() {
        return newPasswordInput;
    }

    public void setNewPasswordInput(UIComponent newPasswordInput) {
        this.newPasswordInput = newPasswordInput;
    }
}
