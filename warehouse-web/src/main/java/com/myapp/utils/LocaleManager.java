package com.myapp.utils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * <p>Created by MontolioV on 21.09.18.
 */
@Named
@SessionScoped
public class LocaleManager implements Serializable {
    private static final long serialVersionUID = 2508031035491269471L;
    @Inject
    private ExternalContext externalContext;
    @Inject
    private UIViewRoot viewRoot;
    private Locale currentLocale;
    private Map<String,Locale> availableLocales = new HashMap<>();

    @PostConstruct
    public void init(){
        availableLocales.put("en", new Locale("en"));
        availableLocales.put("ru", new Locale("ru"));
        availableLocales.put("uk", new Locale("uk"));

        String language = externalContext.getRequestLocale().getLanguage();
        currentLocale = availableLocales.get(language);
    }

    public void changeLocale(ValueChangeEvent event) {
        String newValue = (String) event.getNewValue();
        Locale newLocale = availableLocales.get(newValue);
        viewRoot.setLocale(newLocale);
        currentLocale = newLocale;
    }

    public String test() {
        return String.valueOf(Math.random());
    }

    //Setters & Getters

    public Set<String> getLocaleShortcuts() {
        return availableLocales.keySet();
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(Locale currentLocale) {
        this.currentLocale = currentLocale;
    }
}
