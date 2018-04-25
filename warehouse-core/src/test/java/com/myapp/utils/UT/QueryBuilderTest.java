package com.myapp.utils.UT;

import com.myapp.storing.Item;
import com.myapp.storing.Item_;
import com.myapp.storing.Tag;
import com.myapp.utils.QueryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


/**
 * <p>Created by MontolioV on 24.04.18.
 */
public class QueryBuilderTest {
    private QueryBuilder<Integer> queryBuilder;
    private EntityManager emMock;
    private CriteriaBuilder cbMock;
    private CriteriaQuery<Item> cqMock;
    private Root<Item> riMock;
    private ListJoin<Item, Tag> joinMock;
    private Predicate andPredicateMock;
    private Predicate orPredicateMock;
    private Predicate inPredicateMock;
    private Integer value1 = 1;
    private Integer value2 = 2;
    private ArrayList<Predicate> predicates;

    @Before
    public void setUp() throws Exception {
        emMock = mock(EntityManager.class);
        cbMock = mock(CriteriaBuilder.class);
        cqMock = (CriteriaQuery<Item>) mock(CriteriaQuery.class);
        riMock = (Root<Item>) mock(Root.class);
        joinMock = (ListJoin<Item, Tag>) mock(ListJoin.class);
        andPredicateMock = mock(Predicate.class);
        orPredicateMock = mock(Predicate.class);
        inPredicateMock = mock(Predicate.class);

        when(emMock.getCriteriaBuilder()).thenReturn(cbMock);
        when(cbMock.createQuery(Item.class)).thenReturn(cqMock);
        when(riMock.join(Item_.tags)).thenReturn(joinMock);
        when(cqMock.from(Item.class)).thenReturn(riMock);
        when(cqMock.select(riMock)).thenReturn(cqMock);

        predicates = new ArrayList<>();
        predicates.add(inPredicateMock);
        predicates.add(inPredicateMock);

        queryBuilder = new QueryBuilder<Integer>(emMock);
    }

    @Test
    public void construction() {
        assertThat(queryBuilder.getCriteriaBuilder(), is(cbMock));
        assertThat(queryBuilder.getCriteriaQuery(), is(cqMock));
        assertThat(queryBuilder.getItemRoot(),is(riMock));
        assertThat(queryBuilder.getTagJoin(), is(joinMock));
        assertTrue(queryBuilder.getWherePredicates().isEmpty());
    }

//    @Test
//    public void selectPredicateTarget() {
//        Path pathItemNameMock = mock(Path.class);
//        Path pathItemOwnerMock = mock(Path.class);
//        Path pathTagNameMock = mock(Path.class);
//
//        when(riMock.get(Item_.name)).thenReturn(pathItemNameMock);
//        when(riMock.get(Item_.owner)).thenReturn(pathItemOwnerMock);
//        when(joinMock.get(Tag_.name)).thenReturn(pathTagNameMock);
//
//        for (QueryTarget queryTarget : QueryTarget.values()) {
//            queryBuilder.selectPredicateTarget(queryTarget);
//
//            Path<Integer> fieldPath = queryBuilder.getFieldPath();
//            switch (queryTarget) {
//
//                case ITEM_NAME:
//                    assertThat(fieldPath, is(pathItemNameMock));
//                    break;
//                case ITEM_OWNER:
//                    assertThat(fieldPath, is(pathItemOwnerMock));
//                    break;
//                case TAG_NAME:
//                    assertThat(fieldPath, is(pathTagNameMock));
//                    break;
//                default:
//                    fail("Missing QueryTarget check for " + queryTarget.name());
//            }
//        }
//    }

//    @Test
//    public void constructPredicatesLike() {
//        Predicate likePredicateMock = mock(Predicate.class);
//        when(cbMock.like((Expression<String>) any(Expression.class), any(String.class))).thenReturn(likePredicateMock);
//
//        queryBuilder.constructStrictPredicates(true, value1, value2);
//
//        assertThat(queryBuilder.getPredicateList().size(), is(2));
//        assertThat(queryBuilder.getPredicateList().get(0), is(likePredicateMock));
//        assertThat(queryBuilder.getPredicateList().get(1), is(likePredicateMock));
//    }

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
    public void constructQuery() {
        Predicate predicate = mock(Predicate.class);
        List<Predicate> wherePredicates = new ArrayList<>();
        wherePredicates.add(predicate);
        wherePredicates.add(predicate);
        queryBuilder.setWherePredicates(wherePredicates);

        queryBuilder.constructQuery();
        verify(cqMock).where(predicate, predicate);
    }
}