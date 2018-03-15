package security.IT;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * <p>Created by MontolioV on 15.03.18.
 */
@Ignore
public class EJB_IT {
    private static EJBContainer ejbContainer;

    public EJB_IT() {
    }

    @BeforeClass
    public static void init() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(EJBContainer.MODULES, new File("target/classes"));
        ejbContainer = EJBContainer.createEJBContainer(properties);
    }

    @AfterClass
    public static void clean() {
        ejbContainer.close();
    }


    @Test
    public void ejbValidation() throws NamingException {
        Context context = ejbContainer.getContext();

        assertNotNull(context.lookup("java:global/classes/AccountStore"));
        assertNotNull(context.lookup("java:global/classes/AuthenticationController"));
        assertNotNull(context.lookup("java:global/classes/CustomHttpAuthenticationMechanism"));
        assertNotNull(context.lookup("java:global/classes/CustomIdentityStore"));
        assertNotNull(context.lookup("java:global/classes/CustomRememberMeIdentityStore"));
        assertNotNull(context.lookup("java:global/classes/Encryptor"));
        assertNotNull(context.lookup("java:global/classes/RegistrationController"));
        assertNotNull(context.lookup("java:global/classes/TokenScheduler"));
        assertNotNull(context.lookup("java:global/classes/TokenStore"));
    }
}
