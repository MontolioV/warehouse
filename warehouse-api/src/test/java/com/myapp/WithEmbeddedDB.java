package com.myapp;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class WithEmbeddedDB {
    public EntityManager em;
    public EntityTransaction transaction;

    public WithEmbeddedDB() {
        em = Persistence.createEntityManagerFactory("it-pu").createEntityManager();
        transaction = em.getTransaction();
        try {
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
            System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");InitialContext ic = new InitialContext();
            ic.bind("jdbc/warehouse", em);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
