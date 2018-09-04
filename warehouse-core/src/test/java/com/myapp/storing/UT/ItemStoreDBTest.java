package com.myapp.storing.UT;

import com.myapp.storing.FileItem;
import com.myapp.storing.Item;
import com.myapp.storing.ItemStoreDB;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import java.security.Principal;
import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.myapp.storing.Item.OWNER_PARAM;
import static com.google.common.collect.Lists.newArrayList;
import static com.myapp.utils.TestSecurityConstants.LOGIN_INVALID;
import static com.myapp.utils.TestSecurityConstants.LOGIN_VALID;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertTrue;
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
    private SessionContext scMock;
    @Mock
    private Principal principalMock;
    @Mock
    private TypedQuery<Item> queryMock;
    private Item itemMock;
    private List<Item> items;
    private Item expectedItem1;
    private Item expectedItem2;

    @Before
    public void setUp() throws Exception {
        itemMock = mock(Item.class);
        items = new ArrayList<>();
        when(emMock.find(eq(Item.class), anyLong())).thenReturn(itemMock);
        when(queryMock.getResultList()).thenReturn(items);
        when(scMock.getCallerPrincipal()).thenReturn(principalMock);
        when(principalMock.getName()).thenReturn(LOGIN_VALID);
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
    public void getAllAccessibleItems() {
        TypedQuery<Item> ownerQueryMock = mock(TypedQuery.class);
        when(emMock.createNamedQuery(Item.GET_ALL_ACCESSIBLE, Item.class)).thenReturn(queryMock);
        when(queryMock.setParameter(OWNER_PARAM,LOGIN_VALID)).thenReturn(ownerQueryMock);
        when(ownerQueryMock.getResultList()).thenReturn(items);

        List<Item> allAccessibleItems = itemStoreDB.getAllAccessibleItems();
        assertThat(allAccessibleItems, sameInstance(items));
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
    public void getHashesOfFileItems() {
        TypedQuery<String> queryHashesMock = mock(TypedQuery.class);
        ArrayList<String> hashes = new ArrayList<>();
        hashes.add("1");
        hashes.add("1");
        hashes.add("2");
        when(emMock.createNamedQuery(FileItem.GET_ALL_HASHES, String.class)).thenReturn(queryHashesMock);
        when(queryHashesMock.getResultList()).thenReturn(hashes);

        Set<String> hashesOfFileItems = itemStoreDB.getHashesOfFileItems();
        assertThat(hashesOfFileItems.size(), is(2));
        assertTrue(hashesOfFileItems.contains("1"));
        assertTrue(hashesOfFileItems.contains("2"));
    }

    @Test
    public void saveItems() {
        itemStoreDB.persistItems(new Item(), new Item());
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
    public void deleteItemByOwner() {
        when(itemMock.getOwner()).thenReturn(LOGIN_VALID);

        itemStoreDB.deleteItemByOwner(1, LOGIN_INVALID);
        verify(emMock, never()).remove(itemMock);

        itemStoreDB.deleteItemByOwner(1, LOGIN_VALID);
        verify(emMock).remove(itemMock);
    }

    private void populateList() {
        expectedItem1 = mock(Item.class);
        when(expectedItem1.isShared()).thenReturn(false);
        when(expectedItem1.getOwner()).thenReturn(LOGIN_VALID);
        expectedItem2 = mock(Item.class);
        when(expectedItem2.isShared()).thenReturn(true);
        when(expectedItem2.getOwner()).thenReturn(LOGIN_INVALID);
        Item unexpectedItem = mock(Item.class);
        when(unexpectedItem.isShared()).thenReturn(false);
        when(unexpectedItem.getOwner()).thenReturn(LOGIN_INVALID);

        items = newArrayList(expectedItem1, expectedItem2, unexpectedItem);
        when(queryMock.getResultList()).thenReturn(items);
    }

    @Test
    public void executeCustomSelectQuery() {
        populateList();
        CriteriaQuery<Item> cqMock = mock(CriteriaQuery.class);
        when(emMock.createQuery(cqMock)).thenReturn(queryMock);

        List<Item> result = itemStoreDB.executeCustomSelectQuery(cqMock);
        assertThat(result, containsInAnyOrder(expectedItem1, expectedItem2));
    }

    @Test
    public void executeCustomSelectQueryPredicate() {
        populateList();
        Predicate predicateMock = mock(Predicate.class);
        CriteriaBuilder cbMock = mock(CriteriaBuilder.class);
        CriteriaQuery<Item> cqMock = mock(CriteriaQuery.class);
        when(emMock.getCriteriaBuilder()).thenReturn(cbMock);
        when(cbMock.createQuery(Item.class)).thenReturn(cqMock);
        when(cqMock.where(predicateMock)).thenReturn(cqMock);
        when(emMock.createQuery(cqMock)).thenReturn(queryMock);

        List<Item> result = itemStoreDB.executeCustomSelectQuery(predicateMock);
        verify(cqMock).where(predicateMock);
        assertThat(result, containsInAnyOrder(expectedItem1, expectedItem2));
    }

    @Test
    public void deleteOldItemsWithNoOwner() {
        Item itemMock = mock(Item.class);
        TypedQuery<Item> expQueryMock = mock(TypedQuery.class);
        items.add(itemMock);
        when(emMock.createNamedQuery(Item.GET_EXPIRED, Item.class)).thenReturn(expQueryMock);
        when(expQueryMock.setParameter(eq(Item.MINIMAL_CREATION_DATE_PARAM), any(Date.class))).thenReturn(queryMock);

        Instant instant = Instant.now().minus(1, ChronoUnit.DAYS);
        Date date = Date.from(instant);
        itemStoreDB.deleteOldItemsWithNoOwner(instant);

        ArgumentCaptor<Date> dateArgumentCaptor = ArgumentCaptor.forClass(Date.class);
        verify(expQueryMock).setParameter(eq(Item.MINIMAL_CREATION_DATE_PARAM), dateArgumentCaptor.capture());
        assertThat(dateArgumentCaptor.getValue(), is(date));
        verify(emMock).remove(itemMock);
    }
}