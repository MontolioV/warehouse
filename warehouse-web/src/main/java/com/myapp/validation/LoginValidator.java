package com.myapp.validation;

import com.myapp.security.AccountStore;
import org.apache.commons.lang3.StringUtils;

import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * <p>Created by MontolioV on 20.09.18.
 */
@Model
public class LoginValidator implements Validator<String> {
    @EJB
    private AccountStore accountStore;

    @Override
    public void validate(FacesContext context, UIComponent component, String value) throws ValidatorException {
        if (StringUtils.isBlank(value)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Enter login, please!", null));
        }
        if (accountStore.getAccountByLogin(value).isPresent()) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login already taken",null));
        }
    }
}
