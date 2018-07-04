package com.myapp.storing;

import com.myapp.utils.QueryTarget;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This query builder can
 * <p>Created by MontolioV on 24.04.18.
 */
@Stateless
public class ItemTagQueryBuilder<T> {
    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager em;
    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<Item> itemCriteriaQuery;
    private CriteriaQuery<Tag> tagCriteriaQuery;
    private Root<Item> itemRoot;
    private Root<Tag> tagRoot;
    private ListJoin<Item, Tag> tagJoin;
    private List<Predicate> wherePredicates = new ArrayList<>();
    private Path<T> fieldPath = null;
    private List<Predicate> predicateList = new ArrayList<>();

    @PostConstruct
    public void init() {
        criteriaBuilder = em.getCriteriaBuilder();
        itemCriteriaQuery = criteriaBuilder.createQuery(Item.class);
        tagCriteriaQuery = criteriaBuilder.createQuery(Tag.class);
        itemRoot = itemCriteriaQuery.from(Item.class);
        tagRoot = tagCriteriaQuery.from(Tag.class);
        tagJoin = itemRoot.join(Item_.tags, JoinType.LEFT);
    }

    public void selectPredicateTarget(QueryTarget queryTarget) {
        // TODO: 25.04.18 Realisation
    }

    public void constructStrictPredicates(T... values) {
        for (T value : values) {
            predicateList.add(fieldPath.in(value));
        }
    }

    public void generateWherePredicates(boolean conjunction) {
        Predicate[] array = predicateList.toArray(new Predicate[0]);
        if (conjunction) {
            wherePredicates.add(criteriaBuilder.and(array));
        } else {
            wherePredicates.add(criteriaBuilder.or(array));
        }
        predicateList = new ArrayList<>();
    }

    public void addWherePredicates(List<Predicate> predicates) {
        wherePredicates.addAll(predicates);
    }

    public CriteriaQuery<Item> constructItemQuery() {
        if (wherePredicates.isEmpty()) {
            return null;
        }
        constructQuery(itemCriteriaQuery);
        return itemCriteriaQuery;
    }

    public CriteriaQuery<Tag> constructTagQuery() {
        if (wherePredicates.isEmpty()) {
            return null;
        }
        constructQuery(tagCriteriaQuery);
        return tagCriteriaQuery;
    }

    private void constructQuery(CriteriaQuery criteriaQuery) {
        Predicate[] predicates = wherePredicates.toArray(new Predicate[0]);
        wherePredicates = new ArrayList<>();
        criteriaQuery.where(predicates);
        criteriaQuery.distinct(true);
    }

    //Getters & Setters

    public CriteriaBuilder getCriteriaBuilder() {
        return criteriaBuilder;
    }

    public void setCriteriaBuilder(CriteriaBuilder criteriaBuilder) {
        this.criteriaBuilder = criteriaBuilder;
    }

    public CriteriaQuery<Item> getItemCriteriaQuery() {
        return itemCriteriaQuery;
    }

    public void setItemCriteriaQuery(CriteriaQuery<Item> itemCriteriaQuery) {
        this.itemCriteriaQuery = itemCriteriaQuery;
    }

    public CriteriaQuery<Tag> getTagCriteriaQuery() {
        return tagCriteriaQuery;
    }

    public void setTagCriteriaQuery(CriteriaQuery<Tag> tagCriteriaQuery) {
        this.tagCriteriaQuery = tagCriteriaQuery;
    }

    public Root<Item> getItemRoot() {
        return itemRoot;
    }

    public void setItemRoot(Root<Item> itemRoot) {
        this.itemRoot = itemRoot;
    }

    public Root<Tag> getTagRoot() {
        return tagRoot;
    }

    public void setTagRoot(Root<Tag> tagRoot) {
        this.tagRoot = tagRoot;
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

    public Path<T> getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(Path<T> fieldPath) {
        this.fieldPath = fieldPath;
    }

    public List<Predicate> getPredicateList() {
        return predicateList;
    }

    public void setPredicateList(List<Predicate> predicateList) {
        this.predicateList = predicateList;
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }
}
