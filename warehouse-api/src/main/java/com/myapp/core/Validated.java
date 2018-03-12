package com.myapp.core;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>Created by MontolioV on 09.03.18.
 */
@Qualifier
@Retention(RUNTIME)
@Target(TYPE)
public @interface Validated {
}
