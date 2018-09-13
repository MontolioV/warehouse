package com.myapp.storing.UT;

import com.myapp.storing.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.primefaces.model.DefaultOrganigramNode;
import org.primefaces.model.OrganigramNode;

import javax.faces.context.ExternalContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.myapp.utils.TestSecurityConstants.LOGIN_VALID;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 04.09.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemSearchTest {
    @InjectMocks
    private ItemSearch itemSearch;
    @Mock
    private ExternalContext ecMock;
    @Mock
    private QueryPredicateFactory pfMock;
    @Mock
    private ItemStore isMock;
    @Mock
    private Principal principalMock;
    @Mock
    private CriteriaQuery<Item> queryMock;
    @Captor
    private ArgumentCaptor<Collection<Predicate>> disjCaptor;
    @Captor
    private ArgumentCaptor<Collection<Predicate>> conjCaptor;

    @Before
    public void setUp() throws Exception {
        when(ecMock.getUserPrincipal()).thenReturn(principalMock);
        when(principalMock.getName()).thenReturn(LOGIN_VALID);

    }

    @Test
    public void init() {
        itemSearch.init();
        assertThat(itemSearch.getItemTypes(), containsInAnyOrder(TextItem.class.getSimpleName(), FileItem.class.getSimpleName()));
    }

    @Test
    public void organigramInitWithNoParams() {
        itemSearch.organigramInit();
        OrganigramNode rootNode = itemSearch.getRootNode();
        Condition condition = (Condition) rootNode.getData();
        assertThat(condition.getConditionType(), is(ConditionType.AND));

        List<OrganigramNode> children = rootNode.getChildren();
        assertThat(children.size(), is(1));
        condition = (Condition) children.get(0).getData();
        assertThat(condition.getConditionType(), is(ConditionType.OWNER));
        assertThat((condition.getObject()), is(LOGIN_VALID));
        assertFalse((condition.isLike()));
    }

    @Test
    public void organigramInitWithParams() {
        itemSearch.setItemNameParam("name");
        itemSearch.setItemOwnerParam("owner");
        itemSearch.setTagParam("tag");
        itemSearch.organigramInit();
        OrganigramNode rootNode = itemSearch.getRootNode();
        Condition condition = (Condition) rootNode.getData();
        assertThat(condition.getConditionType(), is(ConditionType.AND));
        assertTrue(rootNode.isDroppable());
        assertTrue(rootNode.isDroppable());
        assertTrue(rootNode.isSelectable());

        List<OrganigramNode> children = rootNode.getChildren();
        assertThat(children.size(), is(3));

        condition = (Condition) children.get(0).getData();
        assertThat(condition.getConditionType(), is(ConditionType.NAME));
        assertThat((condition.getObject()), is("name"));
        assertFalse((condition.isLike()));

        condition = (Condition) children.get(1).getData();
        assertThat(condition.getConditionType(), is(ConditionType.OWNER));
        assertThat((condition.getObject()), is("owner"));
        assertFalse((condition.isLike()));

        condition = (Condition) children.get(2).getData();
        assertThat(condition.getConditionType(), is(ConditionType.TAG));
        assertThat((condition.getObject()), is("tag"));
        assertFalse((condition.isLike()));
    }

    @Test
    public void resetRoot() {
        OrganigramNode oNodeMock = mock(OrganigramNode.class);
        itemSearch.setRootNode(oNodeMock);

        itemSearch.resetRoot();
        verify(oNodeMock).setChildren(eq(newArrayList()));
    }

    @Test
    public void removeSelectedNode() {
        itemSearch.organigramInit();
        List<OrganigramNode> children = itemSearch.getRootNode().getChildren();
        itemSearch.setSelectedNode(children.get(0));
        itemSearch.removeSelectedNode();

        assertThat(children.size(), is(0));
    }

    @Test
    public void addNode() {
        itemSearch.organigramInit();
        itemSearch.setSelectedNode(itemSearch.getRootNode());

        addInternalNodeAssert(ConditionType.AND, 1);
        addInternalNodeAssert(ConditionType.NOT, 2);
        addInternalNodeAssert(ConditionType.OR, 3);
        addLeafNodeAssert(ConditionType.NAME, 4);
        addLeafNodeAssert(ConditionType.OWNER, 5);
        addLeafNodeAssert(ConditionType.TAG, 6);
        addLeafNodeAssert(ConditionType.DATE, 7);

    }
    private void addInternalNodeAssert(ConditionType cType, int idx) {
        addNodeAssert(cType, idx, ItemSearch.INTERNAL, true, true, true, true);
    }
    private void addLeafNodeAssert(ConditionType cType, int idx) {
        addNodeAssert(cType, idx, ItemSearch.LEAF, true, false, false, true);
    }
    private void addNodeAssert(ConditionType cType, int idx, String nodeType,
                              boolean mustBeDraggable, boolean mustBeDroppable, 
                              boolean mustBeCollapsible, boolean mustBeSelectable) {
        List<OrganigramNode> children = itemSearch.getRootNode().getChildren();
        Condition condition = new Condition(cType);
        DateInterval initialInterval = itemSearch.getConditionDateInterval();
        itemSearch.setDateInputRendered(true);
        itemSearch.setLikeInputRendered(true);
        itemSearch.setStringInputRendered(true);
        itemSearch.setCondition(condition);
        itemSearch.addNode();
        OrganigramNode newNode = children.get(idx);
        assertThat(newNode.getType(), is(nodeType));
        assertThat(newNode.getData(), sameInstance(condition));
        assertEquals(newNode.isDraggable(), mustBeDraggable);
        assertEquals(newNode.isDroppable(), mustBeDroppable);
        assertEquals(newNode.isCollapsible(), mustBeCollapsible);
        assertEquals(newNode.isSelectable(), mustBeSelectable);
        assertThat(itemSearch.getConditionDateInterval(), notNullValue());
        assertThat(itemSearch.getConditionDateInterval(), not(sameInstance(initialInterval)));
        assertThat(itemSearch.getCondition(), not(condition));
        assertThat(itemSearch.getCondition().getConditionType(), is(ConditionType.AND));
        assertFalse(itemSearch.isDateInputRendered());
        assertFalse(itemSearch.isStringInputRendered());
        assertFalse(itemSearch.isLikeInputRendered());
    }

    @Test
    public void newConditionDialogStateListener() {
        dialogStateListenerAssert(ConditionType.AND, false, false, false);
        dialogStateListenerAssert(ConditionType.NOT, false, false, false);
        dialogStateListenerAssert(ConditionType.OR, false, false, false);
        dialogStateListenerAssert(ConditionType.NAME, false, true, true);
        dialogStateListenerAssert(ConditionType.OWNER, false, true, true);
        dialogStateListenerAssert(ConditionType.TAG, false, true, true);
        dialogStateListenerAssert(ConditionType.DATE, true, false, false);
    }
    private void dialogStateListenerAssert(ConditionType cType,
                                           boolean mustDateInputRendered,
                                           boolean mustLikeInputRendered,
                                           boolean mustStringInputRendered) {
        Condition conditionMock = new Condition(cType);
        itemSearch.setCondition(conditionMock);
        itemSearch.newConditionDialogStateListener();
        assertEquals(itemSearch.isDateInputRendered(), mustDateInputRendered);
        assertEquals(itemSearch.isLikeInputRendered(), mustLikeInputRendered);
        assertEquals(itemSearch.isStringInputRendered(), mustStringInputRendered);
    }

    @Test
    public void parseAndRunQuery() {
        String name = "name";
        String owner = "owner";
        String tag = "tag";
        Date date1 = new Date();
        Date date2 = new Date();
        DateInterval dateInterval = new DateInterval(date1, date2);
        Predicate andPredMock = mock(Predicate.class);
        Predicate orPredMock = mock(Predicate.class);
        Predicate notPredMock = mock(Predicate.class);
        Predicate namePredMock = mock(Predicate.class);
        Predicate ownerPredMock = mock(Predicate.class);
        Predicate tagPredMock = mock(Predicate.class);
        Predicate datePredMock = mock(Predicate.class);
        ArrayList<Item> expectedList = newArrayList();

        when(pfMock.makeItemNameEqualPredicate(name)).thenReturn(namePredMock);
        when(pfMock.makeItemOwnerEqualPredicate(owner)).thenReturn(ownerPredMock);
        when(pfMock.makeItemTagEqualPredicate(tag)).thenReturn(tagPredMock);
        when(pfMock.makeItemCreationDateBetweenPredicate(date1, date2)).thenReturn(datePredMock);
        when(pfMock.makeInversionPredicate(tagPredMock)).thenReturn(notPredMock);
        when(pfMock.makeDisjunctionPredicate(anyCollection())).thenReturn(orPredMock);
        when(pfMock.makeConjunctionPredicate(anyCollection())).thenReturn(andPredMock);
        when(pfMock.makeItemCriteriaQuery(andPredMock)).thenReturn(queryMock);
        when(isMock.executeCustomSelectQuery(queryMock)).thenReturn(expectedList);

        itemSearch.setRootNode(new DefaultOrganigramNode(new Condition(ConditionType.AND)));
        OrganigramNode rootNode = itemSearch.getRootNode();
        new DefaultOrganigramNode(new Condition(ConditionType.DATE, dateInterval), rootNode);
        DefaultOrganigramNode notNode = new DefaultOrganigramNode(new Condition(ConditionType.NOT), rootNode);
        DefaultOrganigramNode orNode = new DefaultOrganigramNode(new Condition(ConditionType.OR), rootNode);
        new DefaultOrganigramNode(new Condition(ConditionType.TAG, tag), notNode);
        new DefaultOrganigramNode(new Condition(ConditionType.NAME, name), orNode);
        new DefaultOrganigramNode(new Condition(ConditionType.OWNER, owner), orNode);

        itemSearch.parseAndRunQuery();

        assertThat(rootNode.getChildCount(), is(3));
        assertThat(itemSearch.getItems(), sameInstance(expectedList));

        verify(pfMock).makeDisjunctionPredicate(disjCaptor.capture());
        assertThat(disjCaptor.getValue(), containsInAnyOrder(namePredMock, ownerPredMock));
        verify(pfMock, times(2)).makeConjunctionPredicate(conjCaptor.capture());
        assertThat(conjCaptor.getAllValues().get(0), containsInAnyOrder(notPredMock));
        assertThat(conjCaptor.getAllValues().get(1), containsInAnyOrder(andPredMock, orPredMock, datePredMock));
    }

    @Test
    public void parseAndRunQueryEmpty() {
        ArrayList<Item> expected = newArrayList();
        when(isMock.getAllAccessibleItems()).thenReturn(expected);

        itemSearch.setRootNode(new DefaultOrganigramNode(new Condition(ConditionType.AND)));
        itemSearch.parseAndRunQuery();
        assertThat(itemSearch.getItems(), sameInstance(expected));
    }

    @Test
    public void filterByDate() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = sdf.parse("2018.01.01 00:00:00");
        Date dateEq = Date.from(date.toInstant());
        Date dateMinus1Sec = Date.from(date.toInstant().minus(1, SECONDS));
        Date datePlus1Day = Date.from(date.toInstant().plus(1, DAYS));
        Date datePlus1DayMinus1Sec = Date.from(date.toInstant().plus(1, DAYS).minus(1, SECONDS));

        assertFalse(itemSearch.filterByDate(dateMinus1Sec, date, null));
        assertFalse(itemSearch.filterByDate(datePlus1Day, date, null));
        assertFalse(itemSearch.filterByDate(null, date, null));

        assertTrue(itemSearch.filterByDate(dateEq, date, null));
        assertTrue(itemSearch.filterByDate(datePlus1DayMinus1Sec, date, null));
        assertTrue(itemSearch.filterByDate(dateEq, null, null));
        assertTrue(itemSearch.filterByDate(null, null, null));
    }
}