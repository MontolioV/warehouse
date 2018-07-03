package com.myapp.security.UT;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;

import javax.security.enterprise.identitystore.CredentialValidationResult;

import static com.myapp.utils.TestSecurityConstants.LOGIN_VALID;
import static com.myapp.utils.TestSecurityConstants.ROLES_STR_SET;

/**
 * <p>Created by MontolioV on 13.03.18.
 */
public interface CommonChecks {

    default void checkAuthenticationValidResult(CredentialValidationResult result) {
        MatcherAssert.assertThat(result.getCallerPrincipal().getName(), CoreMatchers.is(LOGIN_VALID));
        MatcherAssert.assertThat(result.getCallerGroups(), CoreMatchers.is(ROLES_STR_SET));
    }
}
