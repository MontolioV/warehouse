package com.myapp.storing.UT;

import com.myapp.storing.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
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
    private CriteriaQuery cqMock;
    @Mock
    private Predicate expectedPredMock;
    @Mock
    private Predicate intermediatePredMock1;
    @Mock
    private Predicate intermediatePredMock2;
    @Mock
    private Predicate notNullPredMock;
    @Mock
    private Path ii_namePath;
    @Mock
    private Path ii_ownerPath;
    @Mock
    private Path tt_namePath;
    @Mock
    private ListJoin<Item, Tag> ii_tagsJoin;
    @Mock
    private Path it_namePath;
    @Mock
    private Path ii_datePath;
    @Mock
    private Path ii_sharedPath;
    @Mock
    private Path ii_typePath;
    private CriteriaBuilder cbMock;
    private Root<Item> itemRootMock;
    private Root<Tag> tagRootMock;
    private Path pathMock;
    private List<String> values = newArrayList("value1", "value2");

    @BeforeClass
    public static void beforeClass() {
        Item_.name = mock(SingularAttribute.class);
        Item_.owner = mock(SingularAttribute.class);
        Tag_.name = mock(SingularAttribute.class);
        Item_.creationDate = mock(SingularAttribute.class);
        Item_.shared = mock(SingularAttribute.class);
        Item_.dType = mock(SingularAttribute.class);
        Item_.tags = mock(ListAttribute.class);
    }

    @Before
    public void setUp() throws Exception {


        cbMock = mock(CriteriaBuilder.class);
        pathMock = mock(Path.class);
        itemRootMock = (Root<Item>) mock(Root.class);
        tagRootMock = (Root<Tag>) mock(Root.class);
        when(emMock.getCriteriaBuilder()).thenReturn(cbMock);
        when(cbMock.createQuery(Item.class)).thenReturn(cqMock);
        when(cbMock.createQuery(Tag.class)).thenReturn(cqMock);
        when(cqMock.from(Item.class)).thenReturn(itemRootMock);
        when(cqMock.from(Tag.class)).thenReturn(tagRootMock);
        when(cbMock.and(intermediatePredMock1, notNullPredMock)).thenReturn(expectedPredMock);
        when(itemRootMock.get(Item_.name)).thenReturn(ii_namePath);
        when(itemRootMock.get(Item_.owner)).thenReturn(ii_ownerPath);
        when(tagRootMock.get(Tag_.name)).thenReturn(tt_namePath);
        when(itemRootMock.get(Item_.creationDate)).thenReturn(ii_datePath);
        when(itemRootMock.get(Item_.shared)).thenReturn(ii_sharedPath);
        when(itemRootMock.get(Item_.dType)).thenReturn(ii_typePath);
        when(itemRootMock.join(Item_.tags, JoinType.LEFT)).thenReturn(ii_tagsJoin);
        when(ii_tagsJoin.get(Tag_.name)).thenReturn(it_namePath);

        factory.init();
    }

    @Test
    public void init() {
        DefaultQueryPredicateFactory factory = new DefaultQueryPredicateFactory();
        factory.setEm(emMock);

        assertThat(factory.getCriteriaBuilder(), nullValue());
        assertThat(factory.getItemRoot(), nullValue());
        assertThat(factory.getTagRoot(), nullValue());

        factory.init();

        assertThat(factory.getCriteriaBuilder(), is(cbMock));
        assertThat(factory.getItemRoot(), is(itemRootMock));
        assertThat(factory.getTagRoot(), is(tagRootMock));
    }

    @Test
    public void makeItemCriteriaQueryItem() {
        CriteriaQuery<Item> query = factory.makeItemCriteriaQuery(expectedPredMock);
        verify(cqMock).distinct(true);
        verify(cqMock).where(expectedPredMock);
        assertThat(query, sameInstance(cqMock));
    }

    @Test
    public void makeItemCriteriaQueryTag() {
        CriteriaQuery<Tag> query = factory.makeTagCriteriaQuery(expectedPredMock);
        verify(cqMock).distinct(true);
        verify(cqMock).where(expectedPredMock);
        assertThat(query, sameInstance(cqMock));
    }

    @Test
    public void makeItemNameLikePredicate() {
        when(cbMock.isNotNull(ii_namePath)).thenReturn(notNullPredMock);
        when(cbMock.like(ii_namePath, "val%")).thenReturn(intermediatePredMock1);

        Predicate predicate = factory.makeItemNameLikePredicate("'val");
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemOwnerLikePredicate() {
        when(cbMock.isNotNull(ii_ownerPath)).thenReturn(notNullPredMock);
        when(cbMock.like(ii_ownerPath, "val%")).thenReturn(intermediatePredMock1);

        Predicate predicate = factory.makeItemOwnerLikePredicate("'val");
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeTagNameLikePredicate() {
        when(cbMock.isNotNull(tt_namePath)).thenReturn(notNullPredMock);
        when(cbMock.like(tt_namePath, "val%")).thenReturn(intermediatePredMock1);

        Predicate predicate = factory.makeTagNameLikePredicate("'val");
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemTagLikePredicateOne() {
        when(cbMock.like(it_namePath, "val1%")).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeItemTagLikePredicate("'val1");
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemTagLikePredicateMultiple() {
        when(cbMock.like(it_namePath, "val1%")).thenReturn(intermediatePredMock1);
        when(cbMock.like(it_namePath, "val2%")).thenReturn(intermediatePredMock2);
        when(cbMock.and(anyVararg())).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeItemTagLikePredicate(newArrayList("'val1", "'val2"));
        verify(itemRootMock, times(2)).join(Item_.tags, JoinType.LEFT);
        ArgumentCaptor<Predicate> captor = ArgumentCaptor.forClass(Predicate.class);
        verify(cbMock).and(captor.capture());
        assertThat(captor.getAllValues(), containsInAnyOrder(intermediatePredMock1, intermediatePredMock2));
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemNameEqualPredicate() {
        when(cbMock.isNotNull(ii_namePath)).thenReturn(notNullPredMock);
        when(cbMock.equal(ii_namePath, "value")).thenReturn(intermediatePredMock1);

        Predicate predicate = factory.makeItemNameEqualPredicate("value");
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemOwnerEqualPredicate() {
        when(cbMock.isNotNull(ii_ownerPath)).thenReturn(notNullPredMock);
        when(cbMock.equal(ii_ownerPath, "value")).thenReturn(intermediatePredMock1);

        Predicate predicate = factory.makeItemOwnerEqualPredicate("value");
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeTagNameEqualPredicate() {
        when(cbMock.isNotNull(tt_namePath)).thenReturn(notNullPredMock);
        when(cbMock.equal(tt_namePath, "value")).thenReturn(intermediatePredMock1);

        Predicate predicate = factory.makeTagNameEqualPredicate("value");
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemTagEqualPredicateOne() {
        when(cbMock.equal(it_namePath, "val1")).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeItemTagEqualPredicate("val1");
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemTagEqualPredicateMultiple() {
        when(cbMock.equal(it_namePath, "val1")).thenReturn(intermediatePredMock1);
        when(cbMock.equal(it_namePath, "val2")).thenReturn(intermediatePredMock2);
        when(cbMock.and(anyVararg())).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeItemTagEqualPredicate(newArrayList("val1", "val2"));
        verify(itemRootMock, times(2)).join(Item_.tags, JoinType.LEFT);
        ArgumentCaptor<Predicate> captor = ArgumentCaptor.forClass(Predicate.class);
        verify(cbMock).and(captor.capture());
        assertThat(captor.getAllValues(), containsInAnyOrder(intermediatePredMock1, intermediatePredMock2));
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemOwnerInPredicate() {
        when(cbMock.isNotNull(ii_ownerPath)).thenReturn(notNullPredMock);
        when(ii_ownerPath.in(values)).thenReturn(intermediatePredMock1);

        Predicate predicate = factory.makeItemOwnerInPredicate(values);
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemNameInPredicate() {
        when(cbMock.isNotNull(ii_namePath)).thenReturn(notNullPredMock);
        when(ii_namePath.in(values)).thenReturn(intermediatePredMock1);

        Predicate predicate = factory.makeItemNameInPredicate(values);
        verify(cbMock).and(intermediatePredMock1, notNullPredMock);
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeTagNameInPredicate() {
        when(cbMock.isNotNull(tt_namePath)).thenReturn(notNullPredMock);
        when(tt_namePath.in(values)).thenReturn(intermediatePredMock1);

        Predicate predicate = factory.makeTagNameInPredicate(values);
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemCreationDateBetweenPredicate() {
        Date date1 = new Date();
        Date date2 = new Date();
        when(cbMock.isNotNull(ii_datePath)).thenReturn(notNullPredMock);
        when(cbMock.between(ii_datePath, date1, date2)).thenReturn(intermediatePredMock1);

        Predicate predicate = factory.makeItemCreationDateBetweenPredicate(date1, date2);
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemSharedIsTruePredicate() {
        when(cbMock.isTrue(ii_sharedPath)).thenReturn(expectedPredMock);

        Predicate predicate = factory.makeItemSharedIsTruePredicate();
        assertThat(predicate, sameInstance(expectedPredMock));
    }

    @Test
    public void makeItemTypeEqualPredicate() {
        when(cbMock.isNotNull(ii_typePath)).thenReturn(notNullPredMock);
        when(cbMock.equal(ii_typePath, "type")).thenReturn(intermediatePredMock1);

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