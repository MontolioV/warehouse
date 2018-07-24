package com.myapp.IT;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.RollbackException;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

/**
 * <p>Created by MontolioV on 24.07.18.
 */
@RunWith(Arquillian.class)
public abstract class AbstractITArquillianWithEM {
    @PersistenceContext
    protected EntityManager em;
    @Inject
    private UserTransaction transaction;

    protected static WebArchive createDeployment() {
        File libFile = Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("org.apache.commons:commons-lang3").withTransitivity().asSingleFile();
        WebArchive javaArchive = ShrinkWrap.create(WebArchive.class)
                .addClasses(AbstractITArquillianWithEM.class)
                .addAsLibraries(libFile)
                .addAsWebInfResource("test-persistence.xml", "classes/META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new StringAsset("<web-app></web-app>"), "web.xml");
        return javaArchive;
    }

    @Before
    public void setUp() throws Exception {
        beginTransaction();
    }

    @After
    public void tearDown() throws Exception {
        commitTransaction();
    }

    private void beginTransaction() throws Exception {
        transaction.begin();
        em.joinTransaction();
    }

    private void commitTransaction() throws Exception {
        transaction.commit();
        em.clear();
    }


    /**
     *
     * @param objects to persist
     * @return amount of successfully persisted objects
     * @throws Exception
     */
    protected int persistNotAllowed(List<?> objects) throws Exception {
        int persisted = 0;
        for (Object object : objects) {
            try {
                em.persist(object);
                commitTransaction();
                persisted++;
            } catch (ConstraintViolationException e) {
                transaction.rollback();
            } catch (RollbackException e) {
            } finally {
                beginTransaction();
            }
        }
        return persisted;
    }

    protected void persistAllowed(List<?> objects) throws Exception {
        persistAllowed(objects.toArray());
    }
    protected void persistAllowed(Object... objects) throws Exception {
        try {
            for (Object object : objects) {
                em.persist(object);
            }
        } catch (ConstraintViolationException e) {
            showConstraintViolations(e);
        }
    }
    private void showConstraintViolations(ConstraintViolationException e) throws Exception {
        StringJoiner sj = new StringJoiner("\n");
        Iterator<ConstraintViolation<?>> cvIterator = e.getConstraintViolations().iterator();
        while (cvIterator.hasNext()) {
            ConstraintViolation<?> nextViolation = cvIterator.next();
            sj.add(nextViolation.getMessage());
        }
        throw new Exception(sj.toString(), e);
    }
}
