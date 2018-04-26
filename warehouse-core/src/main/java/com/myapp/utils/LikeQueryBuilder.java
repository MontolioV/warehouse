package com.myapp.utils;

import com.myapp.storing.Item_;
import com.myapp.storing.Tag_;

import javax.ejb.Stateless;

/**
 * <p>Created by MontolioV on 25.04.18.
 */
@Stateless
public class LikeQueryBuilder extends QueryBuilder<String> {

    @Override
    public void selectPredicateTarget(QueryTarget queryTarget) {
        switch (queryTarget) {
            case ITEM_NAME:
                setFieldPath(getItemRoot().get(Item_.name));
                break;
            case ITEM_OWNER:
                setFieldPath(getItemRoot().get(Item_.owner));
                break;
            case TAG_NAME:
                setFieldPath(getTagJoin().get(Tag_.name));
                break;
        }
    }

    public void constructLikePredicates(String... values) {
        for (String value : values) {
            getPredicateList().add(getCriteriaBuilder().like(getFieldPath(), value));
        }
    }
}
