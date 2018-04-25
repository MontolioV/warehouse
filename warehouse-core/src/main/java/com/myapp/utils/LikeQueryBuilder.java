package com.myapp.utils;

import com.myapp.storing.Item_;
import com.myapp.storing.Tag_;

import javax.persistence.EntityManager;

/**
 * <p>Created by MontolioV on 25.04.18.
 */
public class LikeQueryBuilder extends QueryBuilder<String> {

    public LikeQueryBuilder(EntityManager em) {
        super(em);
    }

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
