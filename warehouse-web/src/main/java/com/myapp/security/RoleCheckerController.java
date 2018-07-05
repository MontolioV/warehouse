package com.myapp.security;

import javax.enterprise.inject.Model;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;

/**
 * <p>Created by MontolioV on 19.04.18.
 */
@Model
public class RoleCheckerController {
    @Inject
    private ExternalContext externalContext;

    public boolean checkIsUser() {
        return externalContext.isUserInRole(Roles.USER.name());
    }

    public boolean checkIsModerator() {
        return externalContext.isUserInRole(Roles.MODERATOR.name());
    }

    public boolean checkIsAdmin() {
        return externalContext.isUserInRole(Roles.ADMIN.name());
    }

    public ExternalContext getExternalContext() {
        return externalContext;
    }

    public void setExternalContext(ExternalContext externalContext) {
        this.externalContext = externalContext;
    }
}
