package com.myapp.utils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * <p>Created by MontolioV on 21.09.18.
 */
@Named
@SessionScoped
public class LocaleManager implements Serializable {
    private static final long serialVersionUID = 2508031035491269471L;
    @Inject
    private UIViewRoot viewRoot;
    private Locale currentLocale;
    private Set<Locale> availableLocales = new HashSet<>();

    @PostConstruct
    public void init(){
        availableLocales.add(Locale.US);
        availableLocales.add(new Locale("ru", "RU"));
        availableLocales.add(new Locale("uk", "UA"));
    }

    public void changeLocale(ValueChangeEvent event) {
        System.out.println("!@#");
        Locale locale = (Locale) event.getNewValue();
        System.out.println(locale);
        viewRoot.setLocale(locale);
    }

    //Setters & Getters


    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(Locale currentLocale) {
        this.currentLocale = currentLocale;
    }

    public Set<Locale> getAvailableLocales() {
        return availableLocales;
    }

    public void setAvailableLocales(Set<Locale> availableLocales) {
        this.availableLocales = availableLocales;
    }
}
