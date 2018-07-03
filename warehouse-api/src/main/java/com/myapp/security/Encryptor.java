package com.myapp.security;

/**
 * <p>Created by MontolioV on 03.07.18.
 */
public interface Encryptor {

    String generate(String password);
    boolean verify(String password, String hashedPassword);
}
