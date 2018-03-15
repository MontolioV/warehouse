package com.myapp.security.IT;

import com.myapp.security.*;
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
    public void ejbValidation() throws NamingException, LoginExistsException, UnsecurePasswordException {
        Context context = ejbContainer.getContext();

        assertNotNull(context.lookup("java:global/classes/AccountStore"));
        assertNotNull(context.lookup("java:global/classes/Encryptor"));
        assertNotNull(context.lookup("java:global/classes/TokenScheduler"));
        assertNotNull(context.lookup("java:global/classes/TokenStore"));

        AccountStore accountStore = (AccountStore) context.lookup("java:global/classes/AccountStore");
        assertNotNull(accountStore.getEm());

        Account account = new Account();
        account.setLogin("asdasd");
        account.setPassHash("asdasd");
        account.setEmail("sadasd@asdfjn.com");

        accountStore.createAccount(account);
        Account account1 = accountStore.getAccountByLogin("asdasd").get();
        System.out.println(
                        account1.getId() + "\n" +
                        account1.getPassHash() + "\n" +
                        account1.getEmail() + "\n" +
                        account1.isActive() + "\n" +
                        account1.getRoles() + "\n" +
                        account1.getTokens() + "\n"
        );
    }
}
