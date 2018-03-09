package com.myapp.core;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * <p>Created by MontolioV on 09.03.18.
 */
@Interceptor
@ValidationShown
public class ValidationInterceptorForEJB {

    @AroundInvoke
    public Object processViolationException(InvocationContext ctx) throws Exception {
        try {
            return ctx.proceed();
        } catch (Throwable throwable) {
            Throwable cause = throwable.getCause();
            while (cause != null) {
                if (cause instanceof ConstraintViolationException) {
                    ConstraintViolationException cve = (ConstraintViolationException) cause;
                    String message = aggregateMessage(cve);
                    throw new ExpandedConstraintViolationException(message, throwable);
                }
                cause = cause.getCause();
            }
            throw throwable;
        }
    }

    private String aggregateMessage(ConstraintViolationException cve) {
        Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            sb.append(violation.getMessage()).append("\n");
        }
        return sb.toString();
    }
}
