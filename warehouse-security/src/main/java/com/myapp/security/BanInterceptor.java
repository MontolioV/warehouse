package com.myapp.security;

import javax.annotation.Priority;
import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@BanControl
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class BanInterceptor {
    @EJB
    private AccountStore accountStore;

    @AroundInvoke
    public Object passOnlyNotBanned(InvocationContext ctx)throws Exception {
        if (accountStore.isActiveSelf()) {
            return ctx.proceed();
        } else {
            throw new IllegalStateException("Inactive user can't do this action!");
        }
    }
}
