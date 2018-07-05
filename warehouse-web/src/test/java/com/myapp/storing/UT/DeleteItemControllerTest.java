package com.myapp.storing.UT;

import com.myapp.storing.DeleteItemController;
import com.myapp.storing.FileStoreCleaner;
import com.myapp.storing.ItemStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;

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

    @Test
    public void deleteByID() {
        controller.setId(anyLong());

        controller.deleteByID();
        verify(isMock).deleteAnyItem(anyLong());
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