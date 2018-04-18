package com.myapp.storing.UT;

import com.myapp.storing.DeleteItemController;
import com.myapp.storing.ItemStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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

    @Test
    public void deleteByID() {
        controller.setId(anyLong());

        controller.deleteByID();
        verify(isMock).deleteAnyItem(anyLong());
    }
}