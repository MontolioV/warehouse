package com.myapp.storing;

/**
 * <p>Created by MontolioV on 05.09.18.
 */
public final class Condition {
    private ConditionType conditionType;
    private Object object;
    private boolean isLike = false;

    public Condition(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public Condition(ConditionType conditionType, Object object) {
        this.conditionType = conditionType;
        this.object = object;
    }

    public Condition(ConditionType conditionType, Object object, boolean isLike) {
        this.object = object;
        this.conditionType = conditionType;
        this.isLike = isLike;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    @Override
    public String toString() {
        String result = conditionType.name();
        switch (conditionType) {
            case NAME:
            case OWNER:
            case TAG:
            case DATE:
                if (isLike) {
                    result += "\nlike";
                }
                result += "\n" + object.toString();
        }
        return result;
    }
}
