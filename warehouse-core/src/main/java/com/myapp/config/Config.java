package com.myapp.config;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.TimeZone;

/**
 * <p>Created by MontolioV on 11.09.18.
 */
@Singleton
@Startup
public class Config {

    @PostConstruct
    public void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

}
