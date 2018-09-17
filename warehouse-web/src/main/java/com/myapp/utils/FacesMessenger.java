package com.myapp.utils;

import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import static javax.faces.application.FacesMessage.SEVERITY_INFO;

/**
 * <p>Created by MontolioV on 17.09.18.
 */
@Model
public class FacesMessenger {
    @Inject
    private FacesContext facesContext;

    public void addInfoMessage(String message) {
        facesContext.addMessage(null, new FacesMessage(SEVERITY_INFO, message, null));
    }
}