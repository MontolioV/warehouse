package security.UT;

import com.myapp.security.Roles;

import javax.security.enterprise.identitystore.CredentialValidationResult;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * <p>Created by MontolioV on 13.03.18.
 */
public interface SecurityConstants {
    String LOGIN_VALID = "LOGIN_VALID";
    String LOGIN_INVALID = "LOGIN_INVALID";
    String PASSWORD_VALID = "PASSWORD_VALID_and_reliable_123";
    String PASSWORD_INVALID = "PASSWORD_INVALID";
    String PASS_HASH_VALID = "PASS_HASH_VALID";
    String PASS_HASH_INVALID = "PASS_HASH_INVALID";
    String TOKEN_HASH_VALID = "TOKEN_HASH_VALID";
    String TOKEN_HASH_INVALID = "TOKEN_HASH_INVALID";
    List<Roles> ROLES_LIST = Collections.singletonList(Roles.USER);
    Set<String> ROLES_STR_SET = Collections.singleton(Roles.USER.name());

    default void checkAuthenticationValidResult(CredentialValidationResult result) {
        assertThat(result.getCallerPrincipal().getName(), is(LOGIN_VALID));
        assertThat(result.getCallerGroups(), is(ROLES_STR_SET));
    }
}
