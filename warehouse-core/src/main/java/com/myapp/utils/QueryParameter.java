package com.myapp.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by MontolioV on 25.04.18.
 */
public class QueryParameter<T> {
    private QueryTarget queryTarget;
    private boolean conjunction = false;
    private boolean like = false;
    private List<T> values = new ArrayList<>();

    public QueryParameter(QueryTarget queryTarget, List<T> values) {
        this.queryTarget = queryTarget;
        this.values = values;
    }

    public QueryParameter(QueryTarget queryTarget, boolean conjunction, boolean like, List<T> values) {
        this.queryTarget = queryTarget;
        this.conjunction = conjunction;
        this.like = like;
        this.values = values;
    }

    public QueryTarget getQueryTarget() {
        return queryTarget;
    }

    public void setQueryTarget(QueryTarget queryTarget) {
        this.queryTarget = queryTarget;
    }

    public boolean isConjunction() {
        return conjunction;
    }

    public void setConjunction(boolean conjunction) {
        this.conjunction = conjunction;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public List<T> getValues() {
        return values;
    }

    public void setValues(List<T> values) {
        this.values = values;
    }
}
