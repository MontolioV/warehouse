package com.myapp.utils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * <p>Created by MontolioV on 21.09.18.
 */
@Named
@SessionScoped
public class LocaleManager implements Serializable {
    private static final long serialVersionUID = 8916258104996647192L;
    @Inject
    private ExternalContext externalContext;
    private Locale currentLocale = new Locale("en");
    private Map<String,Locale> availableLocales = new HashMap<>();

    @PostConstruct
    public void init(){
        availableLocales.put("en", new Locale("en"));
        availableLocales.put("ru", new Locale("ru"));
        availableLocales.put("uk", new Locale("uk"));

        if (externalContext.getRequestLocale() != null) {
            String language = externalContext.getRequestLocale().getLanguage();
            if (availableLocales.containsKey(language)) {
                currentLocale = availableLocales.get(language);
            }
        }
    }

    public void changeLocale(String localeCode) {
        Locale newLocale = availableLocales.get(localeCode);
        if (newLocale != null) {
            currentLocale = newLocale;
        }
    }

    //Setters & Getters

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(Locale currentLocale) {
        this.currentLocale = currentLocale;
    }

    public Map<String, Locale> getAvailableLocales() {
        return availableLocales;
    }

    public void setAvailableLocales(Map<String, Locale> availableLocales) {
        this.availableLocales = availableLocales;
    }
}
