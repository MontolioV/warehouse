package com.myapp.utils;

/**
 * Default values for conjunction and like are false.
 * <p>Created by MontolioV on 24.04.18.
 */
public enum QueryParams {
    NAMES(false, false),
    OWNERS(false, false),
    TAGS(false, false),;

    private boolean conjunction;
    private boolean like;

    QueryParams(boolean conjunction, boolean like) {
        this.conjunction = conjunction;
        this.like = like;
    }

    public boolean isConjunction() {
        return conjunction;
    }

    public QueryParams setConjunction(boolean conjunction) {
        this.conjunction = conjunction;
        return this;
    }

    public boolean isLike() {
        return like;
    }

    public QueryParams setLike(boolean like) {
        this.like = like;
        return this;
    }
}
