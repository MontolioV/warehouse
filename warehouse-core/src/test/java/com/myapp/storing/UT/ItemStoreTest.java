package com.myapp.storing.UT;

import com.myapp.storing.Item;
import com.myapp.storing.ItemStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
public class ItemStoreTest {
    @InjectMocks
    private ItemStore itemStore;
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
    }

    @Test
    public void getTenRecentItems() {
        when(emMock.createNamedQuery(Item.GET_LAST_SHARED, Item.class)).thenReturn(queryMock);
        when(queryMock.setFirstResult(anyInt())).thenReturn(queryMock);
        when(queryMock.setMaxResults(anyInt())).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(items);

        List<Item> tenLastItems = itemStore.getTenLastSharedItems();
        assertThat(tenLastItems, sameInstance(items));
    }

    @Test
    public void getItemByIdShared() {
        when(itemMock.isShared()).thenReturn(true);

        Item result = itemStore.getItemById(anyLong(), anyString());
        assertThat(result, sameInstance(itemMock));
    }

    @Test
    public void getItemByIdPersonal() {
        when(itemMock.isShared()).thenReturn(false);
        when(itemMock.getOwner()).thenReturn(LOGIN_VALID);

        Item result = itemStore.getItemById(0, LOGIN_VALID);
        assertThat(result, sameInstance(itemMock));

        result = itemStore.getItemById(0, LOGIN_INVALID);
        assertThat(result, nullValue());
    }

    @Test
    public void getItemByIdMissing() {
        when(emMock.find(eq(Item.class), anyLong())).thenReturn(null);

        Item result = itemStore.getItemById(anyLong(), anyString());
        assertThat(result, nullValue());
    }

    @Test
    public void saveItems() {
        itemStore.saveItems(new Item(), new Item());
        verify(emMock, times(2)).persist(any(Item.class));
    }

    @Test
    public void deleteAnyItem() {
        itemStore.deleteAnyItem(1);
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
        when(queryMock.getResultList()).thenReturn(items);

        itemStore.deleteAllItems();
        verify(emMock, times(3)).remove(any(Item.class));
        verify(emMock).remove(item1);
        verify(emMock).remove(item2);
        verify(emMock).remove(item3);
    }

    @Test
    public void customSelectQuery() {
//        List<Item> items = new ArrayList<>();
//
//        Predicate predicateMock = mock(Predicate.class);
//        CriteriaBuilder cbMock = mock(CriteriaBuilder.class);
//        CriteriaQuery<Item> cqMock = mock(CriteriaQuery.class);
//        Root<Item> rootMock = mock(Root.class);
//        Expression expressionMock = mock(Expression.class);
//        TypedQuery<Item> tqMock = mock(TypedQuery.class);
//
//        when(emMock.getCriteriaBuilder()).thenReturn(cbMock);
//        when(cbMock.createQuery(Item.class)).thenReturn(cqMock);
//        when(cqMock.from(Item.class)).thenReturn(rootMock);
//        when(cqMock.select(rootMock)).thenReturn(cqMock);
//        when(cbMock.and(any(Predicate[].class))).thenReturn(predicateMock);
//        when(cbMock.or(any(Predicate[].class))).thenReturn(predicateMock);
//        when(cbMock.like(any(Expression.class),any(String.class))).thenReturn(predicateMock);
//        when(cqMock.where(any(Predicate[].class))).thenReturn(cqMock);
//        when(rootMock.get(any(SingularAttribute.class))).thenReturn(expressionMock);
//        when(rootMock.in(any(Object[].class))).thenReturn(predicateMock);
//        when(emMock.createQuery(cqMock)).thenReturn(tqMock);
//        when(tqMock.getResultList()).thenReturn(items);
//
//        Map<QueryTarget, String[]> queryParamsMap = new HashMap<>();
//        QueryTarget names = QueryTarget.ITEM_NAME.setLike(true);
//        queryParamsMap.put(names, new String[]{"Foo", "Bar"});
//        QueryTarget tags = QueryTarget.TAG_NAME.setConjunction(true);
//        queryParamsMap.put(tags, new String[]{"one", "two"});
//        queryParamsMap.put(QueryTarget.ITEM_OWNER, new String[]{"owner"});
//
//        List<Item> result = itemStore.customSelectQuery(queryParamsMap);
//        assertThat(result, sameInstance(items));
//        verify(cbMock).like(expressionMock, "Foo");
//        verify(cbMock).like(expressionMock, "Bar");
//        verify(cbMock).or(predicateMock, predicateMock);
//        verify(rootMock).in()
    }
}