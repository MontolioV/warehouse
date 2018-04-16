package com.myapp;

import org.junit.Before;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class WithEmbeddedDB {
    public EntityManager em;
    public EntityTransaction transaction;

    @Before
    public void setUp() throws Exception {
        em = Persistence.createEntityManagerFactory("it-pu").createEntityManager();
        transaction = em.getTransaction();
    }

}
