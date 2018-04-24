package com.myapp.utils.UT;

import com.myapp.storing.Item;
import com.myapp.storing.Item_;
import com.myapp.storing.Tag;
import com.myapp.storing.Tag_;
import com.myapp.utils.ItemsQueryBuilder;
import com.myapp.utils.QueryParams;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


/**
 * <p>Created by MontolioV on 24.04.18.
 */
public class ItemsQueryBuilderTest {
    private ItemsQueryBuilder iqb;
    private EntityManager emMock;
    private CriteriaBuilder cbMock;
    private CriteriaQuery<Item> cqMock;
    private Root<Item> riMock;
    private ListJoin<Item, Tag> joinMock;
    private Path pathMock;
    private Predicate andPredicateMock;
    private Predicate orPredicateMock;
    private Predicate likePredicateMock;
    private Predicate inPredicateMock;
    private String name1 = "name1";
    private String name2 = "name2";
    private String owner1 = "owner1";
    private String owner2 = "owner2";
    private String tag1 = "tag1";
    private String tag2 = "tag2";
    private ArgumentCaptor<Predicate> predArrargumentCaptor;

    @Before
    public void setUp() throws Exception {
        emMock = mock(EntityManager.class);
        andPredicateMock = mock(Predicate.class);
        orPredicateMock = mock(Predicate.class);
        likePredicateMock = mock(Predicate.class);
        inPredicateMock = mock(Predicate.class);

        cbMock = mock(CriteriaBuilder.class);
        cqMock = mock(CriteriaQuery.class);
        riMock = mock(Root.class);
        joinMock = mock(ListJoin.class);
        pathMock = mock(Path.class);

        when(emMock.getCriteriaBuilder()).thenReturn(cbMock);
        when(cbMock.createQuery(Item.class)).thenReturn(cqMock);
        when(riMock.join(Item_.tags)).thenReturn(joinMock);
        when(cqMock.from(Item.class)).thenReturn(riMock);
        when(cqMock.select(riMock)).thenReturn(cqMock);
        when(cqMock.where(any(Predicate[].class))).thenReturn(cqMock);

        when(cbMock.and(anyVararg())).thenReturn(andPredicateMock);
        when(cbMock.or(anyVararg())).thenReturn(orPredicateMock);
        when(cbMock.like(any(Expression.class),any(String.class))).thenReturn(likePredicateMock);
        when(joinMock.get(any(SingularAttribute.class))).thenReturn(pathMock);
        when(riMock.get(any(SingularAttribute.class))).thenReturn(pathMock);
        when(pathMock.in(any(Object[].class))).thenReturn(inPredicateMock);

        iqb = new ItemsQueryBuilder(emMock);
        predArrargumentCaptor = ArgumentCaptor.forClass(Predicate.class);
    }

    @Test
    public void construction() {
        assertThat(iqb.getCriteriaBuilder(), is(cbMock));
        assertThat(iqb.getCriteriaQuery(), is(cqMock));
        assertThat(iqb.getItemRoot(),is(riMock));
        assertThat(iqb.getTagJoin(), is(joinMock));
        assertTrue(iqb.getWherePredicates().isEmpty());
    }

    @Test
    public void addNamesToWhereClause1() {
        iqb.addNamesToWhereClause(QueryParams.NAMES, name1, name2);

        verify(riMock, times(2)).get(Item_.name);
        verify(pathMock).in(name1);
        verify(pathMock).in(name2);
        verify(cbMock).or(predArrargumentCaptor.capture());
        assertPredicates(inPredicateMock);
        assertThat(iqb.getWherePredicates().size(), is(1));
        assertTrue(iqb.getWherePredicates().contains(orPredicateMock));
    }

    @Test
    public void addNamesToWhereClause2() {
        iqb.addNamesToWhereClause(QueryParams.NAMES.setLike(true), name1, name2);

        verify(riMock, times(2)).get(Item_.name);
        verify(cbMock).like(pathMock, name1);
        verify(cbMock).like(pathMock, name2);
        verify(cbMock).or(predArrargumentCaptor.capture());
        assertPredicates(likePredicateMock);
        assertThat(iqb.getWherePredicates().size(), is(1));
        assertTrue(iqb.getWherePredicates().contains(orPredicateMock));
    }

    @Test
    public void addNamesToWhereClause3() {
        iqb.addNamesToWhereClause(QueryParams.NAMES.setConjunction(true), name1, name2);

        verify(riMock, times(2)).get(Item_.name);
        verify(pathMock).in(name1);
        verify(pathMock).in(name2);
        verify(cbMock).and(predArrargumentCaptor.capture());
        assertPredicates(inPredicateMock);
        assertThat(iqb.getWherePredicates().size(), is(1));
        assertTrue(iqb.getWherePredicates().contains(andPredicateMock));
    }

    @Test
    public void addOwnersToWhereClause1() {
        iqb.addOwnersToWhereClause(QueryParams.OWNERS, owner1, owner2);

        verify(riMock, times(2)).get(Item_.owner);
        verify(pathMock).in(owner1);
        verify(pathMock).in(owner2);
        verify(cbMock).or(predArrargumentCaptor.capture());
        assertPredicates(inPredicateMock);
        assertThat(iqb.getWherePredicates().size(), is(1));
        assertTrue(iqb.getWherePredicates().contains(orPredicateMock));
    }

    @Test
    public void addOwnersToWhereClause2() {
        iqb.addOwnersToWhereClause(QueryParams.OWNERS.setLike(true), owner1, owner2);

        verify(riMock, times(2)).get(Item_.owner);
        verify(cbMock).like(pathMock, owner1);
        verify(cbMock).like(pathMock, owner2);
        verify(cbMock).or(predArrargumentCaptor.capture());
        assertPredicates(likePredicateMock);
        assertThat(iqb.getWherePredicates().size(), is(1));
        assertTrue(iqb.getWherePredicates().contains(orPredicateMock));
    }

    @Test
    public void addOwnersToWhereClause3() {
        iqb.addOwnersToWhereClause(QueryParams.OWNERS.setConjunction(true), owner1, owner2);

        verify(riMock, times(2)).get(Item_.owner);
        verify(pathMock).in(owner1);
        verify(pathMock).in(owner2);
        verify(cbMock).and(predArrargumentCaptor.capture());
        assertPredicates(inPredicateMock);
        assertThat(iqb.getWherePredicates().size(), is(1));
        assertTrue(iqb.getWherePredicates().contains(andPredicateMock));
    }

    @Test
    public void addTagsToWhereClause1() {
        iqb.addTagsToWhereClause(QueryParams.TAGS, tag1, tag2);

        verify(joinMock, times(2)).get(Tag_.name);
        verify(pathMock).in(tag1);
        verify(pathMock).in(tag2);
        verify(cbMock).or(predArrargumentCaptor.capture());
        assertPredicates(inPredicateMock);
        assertThat(iqb.getWherePredicates().size(), is(1));
        assertTrue(iqb.getWherePredicates().contains(orPredicateMock));
    }

    @Test
    public void addTagsToWhereClause2() {
        iqb.addTagsToWhereClause(QueryParams.TAGS.setLike(true), tag1, tag2);

        verify(joinMock, times(2)).get(Tag_.name);
        verify(cbMock).like(pathMock, tag1);
        verify(cbMock).like(pathMock, tag2);
        verify(cbMock).or(predArrargumentCaptor.capture());
        assertPredicates(likePredicateMock);
        assertThat(iqb.getWherePredicates().size(), is(1));
        assertTrue(iqb.getWherePredicates().contains(orPredicateMock));
    }

    @Test
    public void addTagsToWhereClause3() {
        iqb.addTagsToWhereClause(QueryParams.TAGS.setConjunction(true), tag1, tag2);

        verify(joinMock, times(2)).get(Tag_.name);
        verify(joinMock).in(tag1);
        verify(joinMock).in(tag2);
        verify(cbMock).and(predArrargumentCaptor.capture());
        assertPredicates(inPredicateMock);
        assertThat(iqb.getWherePredicates().size(), is(1));
        assertTrue(iqb.getWherePredicates().contains(andPredicateMock));
    }

    @Test
    public void constructQuery() {
        Predicate predicate = mock(Predicate.class);
        List<Predicate> wherePredicates = new ArrayList<>();
        wherePredicates.add(predicate);
        iqb.setWherePredicates(wherePredicates);

        CriteriaQuery<Item> criteriaQuery = iqb.constructQuery();
        verify(cqMock).where(predicate);
        assertThat(criteriaQuery, sameInstance(cqMock));
    }

    private void assertPredicates(Predicate predicateMock) {
        List<Predicate> captorAllValues = predArrargumentCaptor.getAllValues();
        assertThat(captorAllValues.size(), is(2));
        captorAllValues.forEach(predicate -> assertThat(predicate, is(predicateMock)));
    }
}