package com.myapp.storing.UT;

import com.myapp.storing.FetchItemsController;
import com.myapp.storing.Item;
import com.myapp.storing.ItemStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 18.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class FetchItemsControllerTest {
    @InjectMocks
    private FetchItemsController controller;
    @Mock
    private ItemStore isMock;

    @Test
    public void fetchRecentItems() {
        ArrayList<Item> items = new ArrayList<>();
        when(isMock.getTenLastItems()).thenReturn(items);

        controller.fetchRecentItems();

        assertThat(controller.getItems(), sameInstance(items));
    }
}