package com.myapp.security;

/**
 * <p>Created by MontolioV on 01.03.18.
 */
public enum Roles {
    ADMIN,
    MODERATOR,
    TRUSTED_USER,
    USER;

    public static class Const {
        public static final String ADMIN = "ADMIN";
        public static final String MODERATOR = "MODERATOR";
        public static final String TRUSTED_USER = "TRUSTED_USER";
        public static final String USER = "USER";
    }

}
