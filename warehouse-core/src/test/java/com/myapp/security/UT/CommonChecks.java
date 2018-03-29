package com.myapp.security.UT;

import javax.security.enterprise.identitystore.CredentialValidationResult;

import static com.myapp.utils.TestSecurityConstants.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * <p>Created by MontolioV on 13.03.18.
 */
public interface CommonChecks {

    default void checkAuthenticationValidResult(CredentialValidationResult result) {
        assertThat(result.getCallerPrincipal().getName(), is(LOGIN_VALID));
        assertThat(result.getCallerGroups(), is(ROLES_STR_SET));
    }
}
