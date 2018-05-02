package com.myapp.storing.UT;

import com.myapp.storing.*;
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
import static org.hamcrest.CoreMatchers.*;
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
    private TagStore tsMock;
    @Mock
    private ItemTagLikeQueryBuilder qbMock;
    @Mock
    private ExternalContext ecMock;
    @Mock
    private Principal principalMock;
    @Mock
    private CriteriaQuery<Item> icqMock;
    private CriteriaQuery<Tag> tcqMock;
    private List<String> strings1 = Arrays.asList("1", "2");
    private List<String> strings2 = Arrays.asList("3", "4");
    private List<String> strings3 = Arrays.asList("5", "6");

    @Before
    public void setUp() throws Exception {
        when(qbMock.constructItemQuery()).thenReturn(icqMock);
        when(qbMock.constructTagQuery()).thenReturn(tcqMock);
        when(ecMock.getUserPrincipal()).thenReturn(principalMock);
        when(principalMock.getName()).thenReturn(LOGIN_VALID);
    }

    @Test
    public void init() {
        controller.init();
        assertTrue(controller.getItemOwners().contains("\"" + LOGIN_VALID + "\""));
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
        when(qbMock.constructItemQuery()).thenReturn(null);

        controller.filteredFetch();
        verify(qbMock, never()).selectPredicateTarget(any());
        verify(qbMock, never()).constructLikePredicates(anyVararg());
        verify(qbMock, never()).generateWherePredicates(anyBoolean());
        verify(qbMock).constructItemQuery();
        verify(isMock, never()).executeCustomSelectQuery(any());
    }

    @Test
    public void filteredFetchItemNames() {
        controller.setItemNames(strings1);
        controller.filteredFetch();

        InOrder inOrder = inOrder(qbMock, isMock);
        inOrder.verify(qbMock).selectPredicateTarget(QueryTarget.ITEM_NAME);
        inOrder.verify(qbMock).constructLikePredicates(strings1.get(0), strings1.get(1));
        inOrder.verify(qbMock).generateWherePredicates(false);
        inOrder.verify(qbMock).constructItemQuery();
        inOrder.verify(isMock).executeCustomSelectQuery(icqMock);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void filteredFetchItemOwners() {
        controller.setItemOwners(strings2);
        controller.filteredFetch();

        InOrder inOrder = inOrder(qbMock, isMock);
        inOrder.verify(qbMock).selectPredicateTarget(QueryTarget.ITEM_OWNER);
        inOrder.verify(qbMock).constructLikePredicates(strings2.get(0), strings2.get(1));
        inOrder.verify(qbMock).generateWherePredicates(false);
        inOrder.verify(qbMock).constructItemQuery();
        inOrder.verify(isMock).executeCustomSelectQuery(icqMock);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void filteredFetchTagsConjunctionFalse() {
        controller.setTagsConjunction(false);
        controller.setTags(strings3);
        controller.filteredFetch();

        InOrder inOrder = inOrder(qbMock, isMock);
        inOrder.verify(qbMock).selectPredicateTarget(QueryTarget.ITEM_JOIN_TAG_NAME);
        inOrder.verify(qbMock).constructLikePredicates(strings3.get(0), strings3.get(1));
        inOrder.verify(qbMock).generateWherePredicates(false);
        inOrder.verify(qbMock).constructItemQuery();
        inOrder.verify(isMock).executeCustomSelectQuery(icqMock);
        inOrder.verifyNoMoreInteractions();
        verify(tsMock, never()).executeCustomSelectQuery(any());
    }

    @Test
    public void filteredFetchTagsConjunctionTrueNoTags() {
        controller.setTagsConjunction(true);
        controller.setTags(null);
        controller.filteredFetch();

        verify(tsMock, never()).executeCustomSelectQuery(any());
    }

    @Test
    public void filteredFetchTagsConjunctionTrue() {
        List<Item> itemsGet = new ArrayList<>();
        List<Tag> tagsGet = new ArrayList<>();
        List<Tag> tagsItem1 = new ArrayList<>();
        List<Tag> tagsItem2 = new ArrayList<>();
        Item itemMock1 = mock(Item.class);
        Item itemMock2 = mock(Item.class);
        Tag tag1 = mock(Tag.class);
        Tag tag2 = mock(Tag.class);
        itemsGet.add(itemMock1);
        itemsGet.add(itemMock2);
        tagsGet.add(tag2);
        tagsGet.add(tag2);
        tagsItem1.add(tag1);
        tagsItem1.add(tag2);
        tagsItem2.add(tag2);
        when(isMock.executeCustomSelectQuery(icqMock)).thenReturn(itemsGet);
        when(tsMock.executeCustomSelectQuery(tcqMock)).thenReturn(tagsGet);
        when(itemMock1.getTags()).thenReturn(tagsItem1);
        when(itemMock2.getTags()).thenReturn(tagsItem2);

        controller.setTagsConjunction(true);
        controller.setTags(strings3);
        controller.filteredFetch();

        InOrder inOrder = inOrder(qbMock, isMock);
        inOrder.verify(qbMock).selectPredicateTarget(QueryTarget.ITEM_JOIN_TAG_NAME);
        inOrder.verify(qbMock).constructLikePredicates(strings3.get(0), strings3.get(1));
        inOrder.verify(qbMock).generateWherePredicates(false);
        inOrder.verify(qbMock).constructItemQuery();
        inOrder.verify(isMock).executeCustomSelectQuery(icqMock);

        inOrder = inOrder(qbMock, tsMock);
        inOrder.verify(qbMock).selectPredicateTarget(QueryTarget.TAG_NAME);
        inOrder.verify(qbMock).constructLikePredicates(strings3.get(0), strings3.get(1));
        inOrder.verify(qbMock).generateWherePredicates(false);
        inOrder.verify(qbMock).constructTagQuery();
        inOrder.verify(tsMock).executeCustomSelectQuery(tcqMock);
        inOrder.verifyNoMoreInteractions();

        assertThat(controller.getItems().size(), is(1));
        assertTrue(controller.getItems().contains(itemMock1));
    }

    @Test
    public void filteredFetchAll() {
        controller.setItemNames(strings1);
        controller.setItemOwners(strings2);
        controller.setTags(strings3);
        controller.setTagsConjunction(false);
        controller.filteredFetch();

        InOrder inOrder = inOrder(qbMock, isMock);

        inOrder.verify(qbMock).selectPredicateTarget(QueryTarget.ITEM_NAME);
        inOrder.verify(qbMock).constructLikePredicates(strings1.get(0), strings1.get(1));
        inOrder.verify(qbMock).generateWherePredicates(false);

        inOrder.verify(qbMock).selectPredicateTarget(QueryTarget.ITEM_OWNER);
        inOrder.verify(qbMock).constructLikePredicates(strings2.get(0), strings2.get(1));
        inOrder.verify(qbMock).generateWherePredicates(false);

        inOrder.verify(qbMock).selectPredicateTarget(QueryTarget.ITEM_JOIN_TAG_NAME);
        inOrder.verify(qbMock).constructLikePredicates(strings3.get(0), strings3.get(1));
        inOrder.verify(qbMock).generateWherePredicates(false);

        inOrder.verify(qbMock).constructItemQuery();
        inOrder.verify(isMock).executeCustomSelectQuery(icqMock);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void castItem() {
        controller.setTextItem(null);
        controller.setFileItem(null);
        Item item1 = new TextItem();
        item1.setdType(TextItem.class.getSimpleName());
        controller.setItem(item1);
        controller.castItem();
        assertThat(controller.getTextItem(), sameInstance(item1));
        assertThat(controller.getFileItem(), nullValue());

        controller.setTextItem(null);
        controller.setFileItem(null);
        Item item2 = new FileItem();
        item2.setdType(FileItem.class.getSimpleName());
        controller.setItem(item2);
        controller.castItem();
        assertThat(controller.getTextItem(), nullValue());
        assertThat(controller.getFileItem(), sameInstance(item2));

        controller.setTextItem(null);
        controller.setFileItem(null);
        controller.setItem(null);
        controller.castItem();
        assertThat(controller.getTextItem(), nullValue());
        assertThat(controller.getFileItem(), nullValue());
    }
}