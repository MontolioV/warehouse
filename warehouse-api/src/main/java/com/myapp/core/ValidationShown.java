package com.myapp.core;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>Created by MontolioV on 09.03.18.
 */
@Retention(RUNTIME)
@Target(TYPE)
@InterceptorBinding
public @interface ValidationShown {
}
