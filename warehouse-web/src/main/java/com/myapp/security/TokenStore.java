package com.myapp.security;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class TokenStore {
    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager entityManager;
    @EJB
    private Encryptor encryptor;
    @EJB
    private AccountStore accountStore;


}
