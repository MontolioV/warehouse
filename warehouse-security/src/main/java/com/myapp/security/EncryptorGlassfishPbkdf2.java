package com.myapp.security;

import org.glassfish.soteria.identitystores.hash.Pbkdf2PasswordHashImpl;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class EncryptorGlassfishPbkdf2 implements Encryptor {
    private Pbkdf2PasswordHashImpl hasher = new Pbkdf2PasswordHashImpl();

    @PostConstruct
    public void init() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA512");
        parameters.put("Pbkdf2PasswordHash.SaltSizeBytes", "64");
        hasher.initialize(parameters);
    }

    @Override
    public String generate(String password) {
        return hasher.generate(password.toCharArray());
    }

    @Override
    public boolean verify(String password, String hashedPassword) {
        return hasher.verify(password.toCharArray(), hashedPassword);
    }

}
