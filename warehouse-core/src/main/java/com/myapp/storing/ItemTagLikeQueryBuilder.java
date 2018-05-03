package com.myapp.storing;

import com.myapp.utils.QueryTarget;

import javax.ejb.Stateless;

/**
 * <p>Created by MontolioV on 25.04.18.
 */
@Stateless
public class ItemTagLikeQueryBuilder extends ItemTagQueryBuilder<String> {

    @Override
    public void selectPredicateTarget(QueryTarget queryTarget) {
        switch (queryTarget) {
            case ITEM_NAME:
                setFieldPath(getItemRoot().get(Item_.name));
                break;
            case ITEM_OWNER:
                setFieldPath(getItemRoot().get(Item_.owner));
                break;
            case ITEM_JOIN_TAG_NAME:
                setFieldPath(getTagJoin().get(Tag_.name));
                break;
            case TAG_NAME:
                setFieldPath(getTagRoot().get(Tag_.name));
                break;
        }
    }

    public void constructLikePredicates(String... values) {
        for (String value : values) {
            getPredicateList().add(getCriteriaBuilder().like(getFieldPath(), adjustString(value)));
        }
    }

    private String adjustString(String s) {
        if (s.startsWith("'") && s.endsWith("'")) {
            return s.substring(1, s.length() - 1);
        } else {
            return "%" + s + "%";
        }
    }
}
