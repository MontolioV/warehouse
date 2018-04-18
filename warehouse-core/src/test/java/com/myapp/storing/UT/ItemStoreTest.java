package com.myapp.storing.UT;

import com.myapp.storing.Item;
import com.myapp.storing.ItemStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 17.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemStoreTest {
    @InjectMocks
    private ItemStore itemStore;
    @Mock
    private EntityManager emMock;
    @Mock
    private TypedQuery<Item> queryMock;
    @Mock
    private Item itemMock;
    private List<Item> items;

    @Test
    public void getTenRecentItems() {
        when(emMock.createNamedQuery(Item.GET_LAST_SHARED, Item.class)).thenReturn(queryMock);
        when(queryMock.setFirstResult(anyInt())).thenReturn(queryMock);
        when(queryMock.setMaxResults(anyInt())).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(items);

        List<Item> tenLastItems = itemStore.getTenLastItems();
        assertThat(tenLastItems, sameInstance(items));
    }

    @Test
    public void saveItems() {
        itemStore.saveItems(new Item(), new Item());
        verify(emMock, times(2)).persist(any(Item.class));
    }

    @Test
    public void deleteAnyItem() {
        when(emMock.find(eq(Item.class), anyLong())).thenReturn(itemMock);

        itemStore.deleteAnyItem(1);
        verify(emMock).remove(itemMock);
    }
}