package com.myapp.security.IT;

import com.myapp.security.IdentityStoreDefault;
import com.myapp.security.RememberMeIdentityStoreDefault;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * <p>Created by MontolioV on 15.03.18.
 */
@Ignore
public class CDI_IT {
    private static Weld weld = new Weld();
    private static WeldContainer container;

    @BeforeClass
    public static void init() {
        container = weld.initialize();
    }

    @AfterClass
    public static void clean() {
        weld.shutdown();
    }

    @Test
    public void getCDIBeans() {
        assertNotNull(container.select(IdentityStoreDefault.class).get());
        assertNotNull(container.select(RememberMeIdentityStoreDefault.class).get());
    }
}
