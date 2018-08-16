package com.myapp.storing.UT;

import com.myapp.storing.DeleteItemController;
import com.myapp.storing.FileStoreCleaner;
import com.myapp.storing.ItemStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.security.Principal;

import static com.myapp.security.Roles.Const.MODERATOR;
import static com.myapp.utils.TestSecurityConstants.LOGIN_VALID;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 18.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeleteItemControllerTest {
    @InjectMocks
    private DeleteItemController controller;
    @Mock
    private ItemStore isMock;
    @Mock
    private FileStoreCleaner fscMock;
    @Mock
    private FacesContext fcMock;
    @Mock
    private ExternalContext ecMock;
    @Mock
    private Principal principalMock;

    @Before
    public void setUp() throws Exception {
        controller.setId(1L);
        when(fcMock.getExternalContext()).thenReturn(ecMock);
        when(ecMock.getUserPrincipal()).thenReturn(principalMock);
        when(principalMock.getName()).thenReturn(LOGIN_VALID);
    }

    @Test
    public void deleteByIDNoRedirectIdNull() {
        controller.setId(null);

        controller.deleteByIDNoRedirect();
        verify(isMock, never()).deleteAnyItem(anyLong());
        verify(isMock, never()).deleteItemByOwner(anyLong(), anyString());
    }

    @Test
    public void deleteByIDNoRedirectUnauthorized() {
        when(ecMock.getUserPrincipal()).thenReturn(null);
        controller.deleteByIDNoRedirect();
        verify(isMock, never()).deleteAnyItem(1L);
        verify(isMock, never()).deleteItemByOwner(anyLong(), anyString());
    }

    @Test
    public void deleteByIDNoRedirectModer() {
        when(ecMock.isUserInRole(MODERATOR)).thenReturn(true);
        controller.deleteByIDNoRedirect();
        verify(isMock).deleteAnyItem(1L);
        verify(isMock, never()).deleteItemByOwner(anyLong(), anyString());
    }

    @Test
    public void deleteByIDNoRedirectOwner() {
        when(ecMock.getUserPrincipal()).thenReturn(principalMock);
        when(principalMock.getName()).thenReturn(LOGIN_VALID);
        controller.deleteByIDNoRedirect();
        verify(isMock, never()).deleteAnyItem(anyLong());
        verify(isMock).deleteItemByOwner(1L, LOGIN_VALID);
    }

    @Test
    public void deleteAll() {
        controller.deleteAll();
        verify(isMock).deleteAllItems();
        verify(fcMock).addMessage(eq(null), any(FacesMessage.class));
    }

    @Test
    public void cleanupFileStorage() {
        controller.cleanupFileStorage();
        verify(fscMock).cleanup();
        verify(fcMock).addMessage(eq(null), any(FacesMessage.class));
    }
}