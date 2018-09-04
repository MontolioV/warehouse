package com.myapp.storing.UT;

import com.myapp.storing.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 04.09.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultQueryPredicateFactoryTest {
    @InjectMocks
    private DefaultQueryPredicateFactory factory;
    @Mock
    private EntityManager emMock;
    @Mock
    private Predicate expectedPredMock;
    private CriteriaBuilder cbMock;
    private Root<Item> itemRootMock;
    private Root<Tag> tagRootMock;
    private ListJoin<Item, Tag> tagJoinMock;
    private Path pathMock;
    private List<String> values = newArrayList("value1", "value2");

    @Before
    public void setUp() throws Exception {
        CriteriaQuery cqMock = mock(CriteriaQuery.class);
        cbMock = mock(CriteriaBuilder.class);
        pathMock = mock(Path.class);
        itemRootMock = (Root<Item>) mock(Root.class);
        tagRootMock = (Root<Tag>) mock(Root.class);
        tagJoinMock = (ListJoin<Item, Tag>) mock(ListJoin.class);
        when(emMock.getCriteriaBuilder()).thenReturn(cbMock);
        when(cbMock.createQuery()).thenReturn(cqMock);
        when(cqMock.from(Item.class)).thenReturn(itemRootMock);
        when(cqMock.from(Tag.class)).thenReturn(tagRootMock);
        when(itemRootMock.join(Item_.tags, JoinType.LEFT)).thenReturn(tagJoinMock);

        factory.init();
    }

    @Test
    public void init() {
        DefaultQueryPredicateFactory factory = new DefaultQueryPredicateFactory();
        factory.setEm(emMock);

        assertThat(factory.getCriteriaBuilder(), nullValue());
        assertThat(factory.getItemRoot(), nullValue());
        assertThat(factory.getTagRoot(), nullValue());
        assertThat(factory.getTagJoin(), nullValue());

        factory.init();

        assertThat(factory.getCriteriaBuilder(), is(cbMock));
        assertThat(factory.getItemRoot(), is(itemRootMock));
        assertThat(factory.getTagRoot(), is(tagRootMock));
        assertThat(factory.getTagJoin(), is(tagJoinMock));
    }

    @Test
    public void makeItemNameLikePredicate() {
        when(cbMock.like(itemRootMock.get(Item_.name), "val%")).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeItemNameLikePredicate("'val");
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemOwnerLikePredicate() {
        when(cbMock.like(itemRootMock.get(Item_.owner), "val%")).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeItemOwnerLikePredicate("'val");
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeTagNameLikePredicate() {
        when(cbMock.like(tagRootMock.get(Tag_.name), "val%")).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeTagNameLikePredicate("'val");
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemJoinTagNameLikePredicate() {
        when(cbMock.like(tagJoinMock.get(Tag_.name), "val%")).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeItemJoinTagNameLikePredicate("'val");
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemOwnerInPredicate() {
        when(itemRootMock.get(Item_.owner)).thenReturn(pathMock);
        when(pathMock.in(values)).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeItemOwnerInPredicate(values);
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemNameInPredicate() {
        when(itemRootMock.get(Item_.name)).thenReturn(pathMock);
        when(pathMock.in(values)).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeItemNameInPredicate(values);
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemJoinTagNameInPredicate() {
        when(tagJoinMock.get(Tag_.name)).thenReturn(pathMock);
        when(pathMock.in(values)).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeItemJoinTagNameInPredicate(values);
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeTagNameInPredicate() {
        when(tagRootMock.get(Tag_.name)).thenReturn(pathMock);
        when(pathMock.in(values)).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeTagNameInPredicate(values);
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemCreationDateBetweenPredicate() {
        Date date1 = new Date();
        Date date2 = new Date();
        when(cbMock.between(itemRootMock.get(Item_.creationDate), date1, date2)).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeItemCreationDateBetweenPredicate(date1, date2);
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemSharedIsTruePredicate() {
        when(cbMock.isTrue(itemRootMock.get(Item_.shared))).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeItemSharedIsTruePredicate();
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemTypeEqualPredicate() {
        when(cbMock.equal(itemRootMock.get(Item_.dType), "type")).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeItemTypeEqualPredicate("type");
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeConjunctionPredicate() {
        Predicate predMock1 = mock(Predicate.class);
        Predicate predMock2 = mock(Predicate.class);
        when(cbMock.and(predMock1, predMock2)).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeConjunctionPredicate(predMock1, predMock2);
        assertThat(predicate, sameInstance(expectedPredMock));

        when(cbMock.and(anyVararg())).thenReturn(expectedPredMock);

        factory.makeConjunctionPredicate(newArrayList(predMock1, predMock2));
        ArgumentCaptor<Predicate> captor = ArgumentCaptor.forClass(Predicate.class);
        verify(cbMock).and(captor.capture());
        assertThat(captor.getAllValues(), containsInAnyOrder(predMock1, predMock2));
    }

    @Test
    public void makeDisjunctionPredicate() {
        Predicate predMock1 = mock(Predicate.class);
        Predicate predMock2 = mock(Predicate.class);
        when(cbMock.or(predMock1, predMock2)).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeDisjunctionPredicate(predMock1, predMock2);
        assertThat(predicate, sameInstance(expectedPredMock));

        when(cbMock.or(anyVararg())).thenReturn(expectedPredMock);

        factory.makeDisjunctionPredicate(newArrayList(predMock1, predMock2));
        ArgumentCaptor<Predicate> captor = ArgumentCaptor.forClass(Predicate.class);
        verify(cbMock).or(captor.capture());
        assertThat(captor.getAllValues(), containsInAnyOrder(predMock1, predMock2));
    }

    @Test
    public void makeInversionPredicate() {
        Predicate predMock1 = mock(Predicate.class);
        when(cbMock.not(predMock1)).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeInversionPredicate(predMock1);
        assertThat(predicate, sameInstance(expectedPredMock));
    }
}