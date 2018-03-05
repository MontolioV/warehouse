package com.myapp.security;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

public class CustomIdentityStore implements IdentityStore {
    @Override
    public CredentialValidationResult validate(Credential credential) {
        return null;
    }
}
