package com.myapp.utils;

import org.primefaces.PrimeFaces;

import javax.enterprise.context.ApplicationScoped;

/**
 * <p>Created by MontolioV on 14.09.18.
 */
@ApplicationScoped
public class PrimeFacesBean {

    public PrimeFaces getInstance() {
        return PrimeFaces.current();
    }
}
