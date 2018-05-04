package com.myapp.navigation;

import javax.enterprise.inject.Model;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import java.io.IOException;

/**
 * <p>Created by MontolioV on 03.05.18.
 */
@Model
public class RedirectController {
    @Inject
    private ExternalContext externalContext;

    public void logoutRedirect() throws IOException {
        String url = externalContext.getApplicationContextPath() + "/logout";
        externalContext.redirect(url);
    }

    public ExternalContext getExternalContext() {
        return externalContext;
    }

    public void setExternalContext(ExternalContext externalContext) {
        this.externalContext = externalContext;
    }
}
