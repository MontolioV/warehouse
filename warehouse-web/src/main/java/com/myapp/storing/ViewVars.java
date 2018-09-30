package com.myapp.storing;

import org.omnifaces.cdi.ViewScoped;

import javax.inject.Named;
import java.io.Serializable;

/**
 * <p>Created by MontolioV on 14.09.18.
 */
@Named
@ViewScoped
public class ViewVars implements Serializable {
    private static final long serialVersionUID = -2020303730971910287L;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
