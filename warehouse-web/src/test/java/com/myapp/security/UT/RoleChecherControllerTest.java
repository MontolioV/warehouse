package com.myapp.security.UT;

import com.myapp.security.RoleChecherController;
import com.myapp.security.Roles;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.context.ExternalContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 19.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class RoleChecherControllerTest {
    @InjectMocks
    private RoleChecherController controller;
    @Mock
    private ExternalContext externalContext;
    
    @Test
    public void isUser() {
        when(externalContext.isUserInRole(Roles.USER.name())).thenReturn(true);
        assertTrue(controller.checkIsUser());
        
        when(externalContext.isUserInRole(Roles.USER.name())).thenReturn(false);
        assertFalse(controller.checkIsUser());
    }

    @Test
    public void isModerator() {
        when(externalContext.isUserInRole(Roles.MODERATOR.name())).thenReturn(true);
        assertTrue(controller.checkIsModerator());

        when(externalContext.isUserInRole(Roles.MODERATOR.name())).thenReturn(false);
        assertFalse(controller.checkIsModerator());
    }

    @Test
    public void isAdmin() {
        when(externalContext.isUserInRole(Roles.ADMIN.name())).thenReturn(true);
        assertTrue(controller.checkIsAdmin());

        when(externalContext.isUserInRole(Roles.ADMIN.name())).thenReturn(false);
        assertFalse(controller.checkIsAdmin());
    }
}