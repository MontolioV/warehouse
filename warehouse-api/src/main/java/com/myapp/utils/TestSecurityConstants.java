package com.myapp.utils;

import com.myapp.security.Roles;

import java.util.Collections;
import java.util.Set;

/**
 * <p>Created by MontolioV on 29.03.18.
 */
public interface TestSecurityConstants {
    String LOGIN_VALID = "LOGIN_VALID";
    String LOGIN_INVALID = "LOGIN_INVALID";
    String PASSWORD_VALID = "PASSWORD_VALID_and_reliable_123";
    String PASSWORD_INVALID = "PASSWORD_INVALID";
    String PASS_HASH_VALID = "PASS_HASH_VALID";
    String PASS_HASH_INVALID = "PASS_HASH_INVALID";
    String TOKEN_HASH_VALID = "TOKEN_HASH_VALID";
    String TOKEN_HASH_INVALID = "TOKEN_HASH_INVALID";
    String EMAIL = "email@email.com";
    Set<Roles> ROLES_SET = Collections.singleton(Roles.USER);
    Set<String> ROLES_STR_SET = Collections.singleton(Roles.USER.name());

}
