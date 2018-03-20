package com.myapp.security;

import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;

/**
 * <p>Created by MontolioV on 20.03.18.
 */
public class FacesContextProducer {

    @Produces
    public FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }
}
