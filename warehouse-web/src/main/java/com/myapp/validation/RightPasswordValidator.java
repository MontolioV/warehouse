package com.myapp.validation;

import com.myapp.security.AccountStore;

import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

/**
 * <p>Created by MontolioV on 23.08.18.
 */
@Model
public class RightPasswordValidator implements Validator<String> {
    @EJB
    private AccountStore accountStore;
    @Inject
    private FacesContext facesContext;

    @Override
    public void validate(FacesContext context, UIComponent component, String value) throws ValidatorException {
        String login = facesContext.getExternalContext().getUserPrincipal().getName();
        boolean passwordValid = accountStore.getAccountByLoginAndPassword(login, value).isPresent();
        if (!passwordValid) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Password is not valid!", null));
        }
    }
}
