package com.myapp.utils;

import com.myapp.storing.Item;
import com.myapp.storing.Item_;
import com.myapp.storing.Tag;
import com.myapp.storing.Tag_;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by MontolioV on 24.04.18.
 */
public class ItemsQueryBuilder {
    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<Item> criteriaQuery;
    private Root<Item> itemRoot;
    private ListJoin<Item, Tag> tagJoin;
    private List<Predicate> wherePredicates = new ArrayList<>();

    public ItemsQueryBuilder(EntityManager em) {
        criteriaBuilder = em.getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(Item.class);
        itemRoot = criteriaQuery.from(Item.class);
        tagJoin = itemRoot.join(Item_.tags);
    }

    public void addNamesToWhereClause(QueryParams namesParam, String... names) {
        List<Predicate> predicateList = new ArrayList<>();
        for (String name : names) {
            if (namesParam.isLike()) {
                predicateList.add(criteriaBuilder.like(itemRoot.get(Item_.name), name));
            } else {
                predicateList.add(itemRoot.get(Item_.name).in(name));
            }
        }
        addPredicateToWherePredicates(predicateList, namesParam.isConjunction());
    }

    public void addOwnersToWhereClause(QueryParams ownersParam, String... owners) {
        List<Predicate> predicateList = new ArrayList<>();
        for (String owner : owners) {
            if (ownersParam.isLike()) {
                predicateList.add(criteriaBuilder.like(itemRoot.get(Item_.owner), owner));
            } else {
                predicateList.add(itemRoot.get(Item_.owner).in(owner));
            }
        }
        addPredicateToWherePredicates(predicateList, ownersParam.isConjunction());
    }

    public void addTagsToWhereClause(QueryParams tagsParam, String... tags) {
        List<Predicate> predicateList = new ArrayList<>();
        for (String tag : tags) {
            if (tagsParam.isLike()) {
                predicateList.add(criteriaBuilder.like(tagJoin.get(Tag_.name), tag));
            } else {
                predicateList.add(tagJoin.get(Tag_.name).in(tag));
            }
        }
        addPredicateToWherePredicates(predicateList, tagsParam.isConjunction());
    }

    private void addPredicateToWherePredicates(List<Predicate> predicateList, boolean conjunction) {
        Predicate[] array = predicateList.toArray(new Predicate[0]);
        if (conjunction) {
            wherePredicates.add(criteriaBuilder.and(array));
        } else {
            wherePredicates.add(criteriaBuilder.or(array));
        }
    }

    public CriteriaQuery<Item> constructQuery() {
        Predicate[] predicates = wherePredicates.toArray(new Predicate[0]);
        return criteriaQuery.where(predicates);
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return criteriaBuilder;
    }

    public void setCriteriaBuilder(CriteriaBuilder criteriaBuilder) {
        this.criteriaBuilder = criteriaBuilder;
    }

    public CriteriaQuery<Item> getCriteriaQuery() {
        return criteriaQuery;
    }

    public void setCriteriaQuery(CriteriaQuery<Item> criteriaQuery) {
        this.criteriaQuery = criteriaQuery;
    }

    public Root<Item> getItemRoot() {
        return itemRoot;
    }

    public void setItemRoot(Root<Item> itemRoot) {
        this.itemRoot = itemRoot;
    }

    public ListJoin<Item, Tag> getTagJoin() {
        return tagJoin;
    }

    public void setTagJoin(ListJoin<Item, Tag> tagJoin) {
        this.tagJoin = tagJoin;
    }

    public List<Predicate> getWherePredicates() {
        return wherePredicates;
    }

    public void setWherePredicates(List<Predicate> wherePredicates) {
        this.wherePredicates = wherePredicates;
    }
}
