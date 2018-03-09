package com.myapp.core.IT;

import com.myapp.core.ValidationInterceptorForEJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;

/**
 * <p>Created by MontolioV on 09.03.18.
 */
@Ignore//ValidationInterceptorForEJB is useless for now
@RunWith(Arquillian.class)
public class ValidationInterceptorForEJBTest {
    @EJB
    private StubEJBForIT stubEJBForIT;

    @Deployment
    public static JavaArchive createArchiveAndDeploy() {
        return ShrinkWrap.create(JavaArchive.class, "ValidationInterceptorForEJB_IT.jar")
                .addClasses(ValidationInterceptorForEJB.class, StubEJBForIT.class)
                .addAsManifestResource("beans.xml");
    }
    @Test
    public void processViolationException() {
        stubEJBForIT.throwValidationException(null, 2);
    }
}