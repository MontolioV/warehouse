package com.myapp.utils;

import javax.enterprise.inject.Produces;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>Created by MontolioV on 14.05.18.
 */
public class HasherProducer {

    @Produces
    @MD5
    public Hasher md5() throws NoSuchAlgorithmException {
        return new Hasher(MessageDigest.getInstance("MD5"));
    }
}
