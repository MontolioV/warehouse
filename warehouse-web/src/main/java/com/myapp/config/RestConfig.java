package com.myapp.config;

import com.myapp.security.RestAccountActivator;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Created by MontolioV on 18.07.18.
 */
@ApplicationPath("rs")
public class RestConfig extends Application {
    private final Set<Class<?>> classSet;

    public RestConfig() {
        HashSet<Class<?>> set = new HashSet<>();
        set.add(RestAccountActivator.class);
        classSet = Collections.unmodifiableSet(set);
    }

    /**
     * Get a set of root resource, provider and {@link Feature feature} classes.
     * <p>
     * The default life-cycle for resource class instances is per-request. The default
     * life-cycle for providers (registered directly or via a feature) is singleton.
     * <p>
     * Implementations should warn about and ignore classes that do not
     * conform to the requirements of root resource or provider/feature classes.
     * Implementations should warn about and ignore classes for which
     * {@link #getSingletons()} returns an instance. Implementations MUST
     * NOT modify the returned set.
     * </p>
     * <p>
     * The default implementation returns an empty set.
     * </p>
     *
     * @return a set of root resource and provider classes. Returning {@code null}
     * is equivalent to returning an empty set.
     */
    @Override
    public Set<Class<?>> getClasses() {
        return classSet;
    }
}
