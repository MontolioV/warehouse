package com.myapp.storing.UT;

import com.myapp.storing.Item;
import com.myapp.storing.ItemTagQueryBuilder;
import com.myapp.storing.Item_;
import com.myapp.storing.Tag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;


/**
 * <p>Created by MontolioV on 24.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemTagQueryBuilderTest {
    @InjectMocks
    private ItemTagQueryBuilder<Integer> queryBuilder;
    @Mock
    private EntityManager emMock;
    protected CriteriaBuilder cbMock;
    protected CriteriaQuery<Item> icqMock;
    protected CriteriaQuery<Tag> tcqMock;
    protected Root<Item> riMock;
    protected Root<Tag> rtMock;
    protected ListJoin<Item, Tag> joinMock;
    private Predicate andPredicateMock;
    private Predicate orPredicateMock;
    private Predicate inPredicateMock;
    private Integer value1 = 1;
    private Integer value2 = 2;
    private ArrayList<Predicate> predicates;

    @Before
    public void setUp() throws Exception {
        cbMock = mock(CriteriaBuilder.class);
        icqMock = (CriteriaQuery<Item>) mock(CriteriaQuery.class);
        tcqMock = (CriteriaQuery<Tag>) mock(CriteriaQuery.class);
        riMock = (Root<Item>) mock(Root.class);
        rtMock = (Root<Tag>) mock(Root.class);
        joinMock = (ListJoin<Item, Tag>) mock(ListJoin.class);
        andPredicateMock = mock(Predicate.class);
        orPredicateMock = mock(Predicate.class);
        inPredicateMock = mock(Predicate.class);

        when(emMock.getCriteriaBuilder()).thenReturn(cbMock);
        when(cbMock.createQuery(Item.class)).thenReturn(icqMock);
        when(cbMock.createQuery(Tag.class)).thenReturn(tcqMock);
        when(riMock.join(Item_.tags, JoinType.LEFT)).thenReturn(joinMock);
        when(icqMock.from(Item.class)).thenReturn(riMock);
        when(tcqMock.from(Tag.class)).thenReturn(rtMock);
        when(icqMock.select(riMock)).thenReturn(icqMock);
        when(icqMock.where(any(Predicate.class), any(Predicate.class))).thenReturn(icqMock);
        when(tcqMock.select(rtMock)).thenReturn(tcqMock);
        when(tcqMock.where(any(Predicate.class), any(Predicate.class))).thenReturn(tcqMock);

        predicates = new ArrayList<>();
        predicates.add(inPredicateMock);
        predicates.add(inPredicateMock);

        queryBuilder.init();
    }

    @Test
    public void construction() {
        assertThat(queryBuilder.getCriteriaBuilder(), is(cbMock));
        assertThat(queryBuilder.getItemCriteriaQuery(), is(icqMock));
        assertThat(queryBuilder.getTagCriteriaQuery(), is(tcqMock));
        assertThat(queryBuilder.getItemRoot(),is(riMock));
        assertThat(queryBuilder.getTagJoin(), is(joinMock));
        assertTrue(queryBuilder.getWherePredicates().isEmpty());
    }

    @Test
    public void constructPredicatesStrict() {
        Path<Integer> pathMock = (Path<Integer>) mock(Path.class);
        when(pathMock.in(Matchers.<Object>anyVararg())).thenReturn(inPredicateMock);

        queryBuilder.setFieldPath(pathMock);
        queryBuilder.constructStrictPredicates(value1, value2);

        assertThat(queryBuilder.getPredicateList().size(), is(2));
        assertThat(queryBuilder.getPredicateList().get(0), is(inPredicateMock));
        assertThat(queryBuilder.getPredicateList().get(1), is(inPredicateMock));
    }

    @Test
    public void aggregatePredicatesAnd() {
        when(cbMock.and(Matchers.<Predicate>anyVararg())).thenReturn(andPredicateMock);

        queryBuilder.setPredicateList(predicates);
        queryBuilder.generateWherePredicates(true);
        assertThat(queryBuilder.getWherePredicates().size(), is(1));
        assertThat(queryBuilder.getWherePredicates().get(0), is(andPredicateMock));
        assertTrue(queryBuilder.getPredicateList().isEmpty());

        queryBuilder.setPredicateList(predicates);
        queryBuilder.generateWherePredicates(true);
        assertThat(queryBuilder.getWherePredicates().size(), is(2));
        assertThat(queryBuilder.getWherePredicates().get(1), is(andPredicateMock));
    }

    @Test
    public void aggregatePredicatesAOr() {
        when(cbMock.or(Matchers.<Predicate>anyVararg())).thenReturn(orPredicateMock);

        queryBuilder.setPredicateList(predicates);
        queryBuilder.generateWherePredicates(false);
        assertThat(queryBuilder.getWherePredicates().size(), is(1));
        assertThat(queryBuilder.getWherePredicates().get(0), is(orPredicateMock));
        assertTrue(queryBuilder.getPredicateList().isEmpty());

        queryBuilder.setPredicateList(predicates);
        queryBuilder.generateWherePredicates(false);
        assertThat(queryBuilder.getWherePredicates().size(), is(2));
        assertThat(queryBuilder.getWherePredicates().get(1), is(orPredicateMock));
    }

    @Test
    public void addWherePredicates() {
        queryBuilder.setWherePredicates(predicates);
        assertThat(queryBuilder.getWherePredicates().size(), is(2));
        queryBuilder.addWherePredicates(predicates);
        assertThat(queryBuilder.getWherePredicates().size(), is(4));
    }

    @Test
    public void constructItemQuery() {
        Predicate predicate = mock(Predicate.class);
        List<Predicate> wherePredicates = new ArrayList<>();
        queryBuilder.setWherePredicates(wherePredicates);

        CriteriaQuery<Item> itemCriteriaQuery = queryBuilder.constructItemQuery();
        assertThat(itemCriteriaQuery, nullValue());

        wherePredicates.add(predicate);
        wherePredicates.add(predicate);
        queryBuilder.setWherePredicates(wherePredicates);

        itemCriteriaQuery= queryBuilder.constructItemQuery();
        assertThat(itemCriteriaQuery, notNullValue());
        assertTrue(queryBuilder.getWherePredicates().isEmpty());
        verify(icqMock).where(predicate, predicate);
        verify(itemCriteriaQuery).distinct(true);
    }

    @Test
    public void constructTagQuery() {
        Predicate predicate = mock(Predicate.class);
        List<Predicate> wherePredicates = new ArrayList<>();
        queryBuilder.setWherePredicates(wherePredicates);

        CriteriaQuery<Tag> tagCriteriaQuery = queryBuilder.constructTagQuery();
        assertThat(tagCriteriaQuery, nullValue());

        wherePredicates.add(predicate);
        wherePredicates.add(predicate);
        queryBuilder.setWherePredicates(wherePredicates);

        tagCriteriaQuery= queryBuilder.constructTagQuery();
        assertThat(tagCriteriaQuery, notNullValue());
        assertTrue(queryBuilder.getWherePredicates().isEmpty());
        verify(tcqMock).where(predicate, predicate);
        verify(tagCriteriaQuery).distinct(true);
    }
}