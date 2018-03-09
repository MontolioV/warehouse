package com.myapp.core.IT;

import com.myapp.core.ValidationShown;

import javax.ejb.Stateless;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * <p>Created by MontolioV on 09.03.18.
 */
@Stateless
@ValidationShown
public class StubEJBForIT {
    public void throwValidationException(@NotNull Object o, @Max(1) int i) {

    }
}
