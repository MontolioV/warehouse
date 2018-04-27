package com.myapp.storing.UT;

import com.myapp.storing.ItemTagLikeQueryBuilder;
import com.myapp.storing.Item_;
import com.myapp.storing.Tag_;
import com.myapp.utils.QueryTarget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.SingularAttribute;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 25.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemTagLikeQueryBuilderTest extends ItemTagQueryBuilderTest{
    @InjectMocks
    private ItemTagLikeQueryBuilder likeQueryBuilder;
    private String value1 = "value1";
    private String value2 = "\"value2\"";

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        likeQueryBuilder.init();
    }

    @Test
    public void selectPredicateTarget() {
        Path<String> pathItemNameMock = (Path<String>) mock(Path.class);
        Path<String> pathItemOwnerMock = (Path<String>) mock(Path.class);
        Path<String> pathJoinTagNameMock = (Path<String>) mock(Path.class);
        Path<String> pathTagNameMock = (Path<String>) mock(Path.class);
        Item_.name = mock(SingularAttribute.class);
        Item_.owner = mock(SingularAttribute.class);
        Tag_.name = mock(SingularAttribute.class);

        when(riMock.get(Item_.name)).thenReturn(pathItemNameMock);
        when(riMock.get(Item_.owner)).thenReturn(pathItemOwnerMock);
        when(joinMock.get(Tag_.name)).thenReturn(pathJoinTagNameMock);
        when(rtMock.get(Tag_.name)).thenReturn(pathTagNameMock);

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
                case ITEM_JOIN_TAG_NAME:
                    assertThat(fieldPath, is(pathJoinTagNameMock));
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
        verify(cbMock).like(any(Expression.class), eq("%value1%"));
        verify(cbMock).like(any(Expression.class), eq("value2"));

    }

}