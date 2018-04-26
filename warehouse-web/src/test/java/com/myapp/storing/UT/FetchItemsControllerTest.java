package com.myapp.storing.UT;

import com.myapp.storing.FetchItemsController;
import com.myapp.storing.Item;
import com.myapp.storing.ItemStore;
import com.myapp.utils.LikeQueryBuilder;
import com.myapp.utils.QueryTarget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.context.ExternalContext;
import javax.persistence.criteria.CriteriaQuery;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.myapp.utils.TestSecurityConstants.LOGIN_VALID;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 18.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class FetchItemsControllerTest {
    @InjectMocks
    private FetchItemsController controller;
    @Mock
    private ItemStore isMock;
    @Mock
    private LikeQueryBuilder qbMock;
    @Mock
    private ExternalContext ecMock;
    @Mock
    private Principal principalMock;
    @Mock
    private CriteriaQuery<Item> cqMock;
    private List<String> strings1 = Arrays.asList("1", "2");
    private List<String> strings2 = Arrays.asList("3", "4");
    private List<String> strings3 = Arrays.asList("5", "6");

    @Before
    public void setUp() throws Exception {
        when(qbMock.constructQuery()).thenReturn(cqMock);
        when(ecMock.getUserPrincipal()).thenReturn(principalMock);
        when(principalMock.getName()).thenReturn(LOGIN_VALID);
    }

    @Test
    public void init() {
        controller.init();
        assertTrue(controller.getItemOwners().contains(LOGIN_VALID));
    }

    @Test
    public void fetchRecentItems() {
        ArrayList<Item> items = new ArrayList<>();
        when(isMock.getTenLastSharedItems()).thenReturn(items);

        controller.fetchRecentItems();

        assertThat(controller.getItems(), sameInstance(items));
    }

    @Test
    public void fetchById() {
        Item item = new Item();
        controller.setId(0L);
        when(isMock.getItemById(0L, LOGIN_VALID)).thenReturn(item);

        controller.fetchById();
        assertThat(controller.getItem(), sameInstance(item));
    }

    @Test
    public void fetchByIdAnonymous() {
        Item item = new Item();
        controller.setId(0L);
        when(ecMock.getUserPrincipal()).thenReturn(null);
        when(isMock.getItemById(0L, null)).thenReturn(item);

        controller.fetchById();
        assertThat(controller.getItem(), sameInstance(item));
    }

    @Test
    public void filteredFetchEmpty() {
        when(qbMock.constructQuery()).thenReturn(null);

        controller.filteredFetch();
        verify(qbMock, never()).selectPredicateTarget(any());
        verify(qbMock, never()).constructLikePredicates(anyVararg());
        verify(qbMock, never()).generateWherePredicates(anyBoolean());
        verify(qbMock).constructQuery();
        verify(isMock, never()).executeCustomSelectQuery(any());
    }

    @Test
    public void filteredFetchItemNames() {
        controller.setItemNames(strings1);
        controller.filteredFetch();

        InOrder inOrder = inOrder(qbMock, isMock);
        inOrder.verify(qbMock).selectPredicateTarget(QueryTarget.ITEM_NAME);
        inOrder.verify(qbMock).constructLikePredicates(strings1.get(0), strings1.get(1));
        inOrder.verify(qbMock).generateWherePredicates(controller.isItemOwnersConjunction());
        inOrder.verify(qbMock).constructQuery();
        inOrder.verify(isMock).executeCustomSelectQuery(cqMock);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void filteredFetchItemOwners() {
        controller.setItemOwners(strings2);
        controller.filteredFetch();

        InOrder inOrder = inOrder(qbMock, isMock);
        inOrder.verify(qbMock).selectPredicateTarget(QueryTarget.ITEM_OWNER);
        inOrder.verify(qbMock).constructLikePredicates(strings2.get(0), strings2.get(1));
        inOrder.verify(qbMock).generateWherePredicates(controller.isItemOwnersConjunction());
        inOrder.verify(qbMock).constructQuery();
        inOrder.verify(isMock).executeCustomSelectQuery(cqMock);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void filteredFetchTags() {
        controller.setTags(strings3);
        controller.filteredFetch();

        InOrder inOrder = inOrder(qbMock, isMock);
        inOrder.verify(qbMock).selectPredicateTarget(QueryTarget.TAG_NAME);
        inOrder.verify(qbMock).constructLikePredicates(strings3.get(0), strings3.get(1));
        inOrder.verify(qbMock).generateWherePredicates(controller.isTagsConjunction());
        inOrder.verify(qbMock).constructQuery();
        inOrder.verify(isMock).executeCustomSelectQuery(cqMock);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void filteredFetchAll() {
        controller.setItemNames(strings1);
        controller.setItemOwners(strings2);
        controller.setTags(strings3);
        controller.filteredFetch();

        InOrder inOrder = inOrder(qbMock, isMock);

        inOrder.verify(qbMock).selectPredicateTarget(QueryTarget.ITEM_NAME);
        inOrder.verify(qbMock).constructLikePredicates(strings1.get(0), strings1.get(1));
        inOrder.verify(qbMock).generateWherePredicates(controller.isItemNamesConjunction());

        inOrder.verify(qbMock).selectPredicateTarget(QueryTarget.ITEM_OWNER);
        inOrder.verify(qbMock).constructLikePredicates(strings2.get(0), strings2.get(1));
        inOrder.verify(qbMock).generateWherePredicates(controller.isItemOwnersConjunction());

        inOrder.verify(qbMock).selectPredicateTarget(QueryTarget.TAG_NAME);
        inOrder.verify(qbMock).constructLikePredicates(strings3.get(0), strings3.get(1));
        inOrder.verify(qbMock).generateWherePredicates(controller.isTagsConjunction());

        inOrder.verify(qbMock).constructQuery();
        inOrder.verify(isMock).executeCustomSelectQuery(cqMock);
        inOrder.verifyNoMoreInteractions();
    }
}