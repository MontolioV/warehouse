package com.myapp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * <p>Created by MontolioV on 11.05.18.
 */
public class Hasher {
    private final MessageDigest messageDigest;

    public Hasher(MessageDigest messageDigest) {
        this.messageDigest = messageDigest;
    }

    public String makeHash(InputStream is) throws IOException {
        byte[] buffer = new byte[(int) Math.pow(2, 20)];
        int read = is.read(buffer);
        while (read != -1) {
            if (read < buffer.length) {
                messageDigest.update(buffer, 0, read);
            } else {
                messageDigest.update(buffer);
            }
            read = is.read(buffer);
        }
        byte[] digest = this.messageDigest.digest();

        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
