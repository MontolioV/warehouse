package com.myapp.utils.UT;

import com.myapp.storing.Item;
import com.myapp.storing.Item_;
import com.myapp.storing.Tag;
import com.myapp.storing.Tag_;
import com.myapp.utils.LikeQueryBuilder;
import com.myapp.utils.QueryTarget;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 25.04.18.
 */
public class LikeQueryBuilderTest {
    private LikeQueryBuilder likeQueryBuilder;
    private EntityManager emMock;
    private CriteriaBuilder cbMock;
    private CriteriaQuery<Item> cqMock;
    private Root<Item> riMock;
    private ListJoin<Item, Tag> joinMock;
//    private Predicate andPredicateMock;
//    private Predicate orPredicateMock;
//    private Predicate inPredicateMock;
    private String value1 = "value1";
    private String value2 = "value2";
//    private ArrayList<Predicate> predicates;

    @Before
    public void setUp() throws Exception {
        emMock = mock(EntityManager.class);
//        andPredicateMock = mock(Predicate.class);
//        orPredicateMock = mock(Predicate.class);
//        inPredicateMock = mock(Predicate.class);
//
        cbMock = mock(CriteriaBuilder.class);
        cqMock = (CriteriaQuery<Item>) mock(CriteriaQuery.class);
        riMock = (Root<Item>) mock(Root.class);
        joinMock = (ListJoin<Item, Tag>) mock(ListJoin.class);

        when(emMock.getCriteriaBuilder()).thenReturn(cbMock);
        when(cbMock.createQuery(Item.class)).thenReturn(cqMock);
        when(riMock.join(Item_.tags)).thenReturn(joinMock);
        when(cqMock.from(Item.class)).thenReturn(riMock);
        when(cqMock.select(riMock)).thenReturn(cqMock);

        likeQueryBuilder = new LikeQueryBuilder(emMock);
//        predicates = new ArrayList<>();
//        predicates.add(inPredicateMock);
//        predicates.add(inPredicateMock);
    }

    @Test
    public void selectPredicateTarget() {
        Path<String> pathItemNameMock = (Path<String>) mock(Path.class);
        Path<String> pathItemOwnerMock = (Path<String>) mock(Path.class);
        Path<String> pathTagNameMock = (Path<String>) mock(Path.class);


        when(riMock.get(Item_.name)).thenReturn(pathItemNameMock);
        when(riMock.get(Item_.owner)).thenReturn(pathItemOwnerMock);
        when(joinMock.get(Tag_.name)).thenReturn(pathTagNameMock);

        for (QueryTarget queryTarget : QueryTarget.values()) {
            likeQueryBuilder.selectPredicateTarget(queryTarget);

            Path<String> fieldPath = likeQueryBuilder.getFieldPath();
            switch (queryTarget) {

                case ITEM_NAME:
                    assertThat(fieldPath, is(pathItemNameMock));
                    break;
                case ITEM_OWNER:
                    assertThat(fieldPath, is(pathItemOwnerMock));
                    break;
                case TAG_NAME:
                    assertThat(fieldPath, is(pathTagNameMock));
                    break;
                default:
                    fail("Missing QueryTarget check for " + queryTarget.name());
            }
        }
    }

    @Test
    public void constructPredicatesLike() {
        Predicate likePredicateMock = mock(Predicate.class);
        when(cbMock.like((Expression<String>) any(Expression.class), any(String.class))).thenReturn(likePredicateMock);

        likeQueryBuilder.constructLikePredicates(value1, value2);

        assertThat(likeQueryBuilder.getPredicateList().size(), is(2));
        assertThat(likeQueryBuilder.getPredicateList().get(0), is(likePredicateMock));
        assertThat(likeQueryBuilder.getPredicateList().get(1), is(likePredicateMock));
    }

}