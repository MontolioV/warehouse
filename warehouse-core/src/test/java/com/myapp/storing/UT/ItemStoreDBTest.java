package com.myapp.storing.UT;

import com.myapp.storing.Item;
import com.myapp.storing.ItemStoreDB;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;

import static com.myapp.utils.TestSecurityConstants.LOGIN_INVALID;
import static com.myapp.utils.TestSecurityConstants.LOGIN_VALID;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 17.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemStoreDBTest {
    @InjectMocks
    private ItemStoreDB itemStoreDB;
    @Mock
    private EntityManager emMock;
    @Mock
    private TypedQuery<Item> queryMock;
    private Item itemMock;
    private List<Item> items;

    @Before
    public void setUp() throws Exception {
        itemMock = mock(Item.class);
        items = new ArrayList<>();
        when(emMock.find(eq(Item.class), anyLong())).thenReturn(itemMock);
        when(queryMock.getResultList()).thenReturn(items);
    }

    @Test
    public void getTenRecentItems() {
        when(emMock.createNamedQuery(Item.GET_LAST_SHARED, Item.class)).thenReturn(queryMock);
        when(queryMock.setFirstResult(anyInt())).thenReturn(queryMock);
        when(queryMock.setMaxResults(anyInt())).thenReturn(queryMock);

        List<Item> tenLastItems = itemStoreDB.getTenLastSharedItems();
        assertThat(tenLastItems, sameInstance(items));
    }

    @Test
    public void getItemByIdShared() {
        when(itemMock.isShared()).thenReturn(true);

        Item result = itemStoreDB.getItemById(anyLong(), anyString());
        assertThat(result, sameInstance(itemMock));
    }

    @Test
    public void getItemByIdPersonal() {
        when(itemMock.isShared()).thenReturn(false);
        when(itemMock.getOwner()).thenReturn(LOGIN_VALID);

        Item result = itemStoreDB.getItemById(0, LOGIN_VALID);
        assertThat(result, sameInstance(itemMock));

        result = itemStoreDB.getItemById(0, LOGIN_INVALID);
        assertThat(result, nullValue());
    }

    @Test
    public void getItemByIdMissing() {
        when(emMock.find(eq(Item.class), anyLong())).thenReturn(null);

        Item result = itemStoreDB.getItemById(anyLong(), anyString());
        assertThat(result, nullValue());
    }

    @Test
    public void saveItems() {
        itemStoreDB.saveItems(new Item(), new Item());
        verify(emMock, times(2)).persist(any(Item.class));
    }

    @Test
    public void deleteAnyItem() {
        itemStoreDB.deleteAnyItem(1);
        verify(emMock).remove(itemMock);
    }

    @Test
    public void deleteAllItems() {
        Item item1 = mock(Item.class);
        Item item2 = mock(Item.class);
        Item item3 = mock(Item.class);
        items.add(item1);
        items.add(item2);
        items.add(item3);
        when(emMock.createNamedQuery(Item.GET_ALL, Item.class)).thenReturn(queryMock);

        itemStoreDB.deleteAllItems();
        verify(emMock, times(3)).remove(any(Item.class));
        verify(emMock).remove(item1);
        verify(emMock).remove(item2);
        verify(emMock).remove(item3);
    }

    @Test
    public void executeCustomSelectQuery() {
        CriteriaQuery<Item> cqMock = mock(CriteriaQuery.class);
        when(emMock.createQuery(cqMock)).thenReturn(queryMock);

        List<Item> result = itemStoreDB.executeCustomSelectQuery(cqMock);
        assertThat(result, sameInstance(items));
    }
}