package com.myapp.storing;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>Created by MontolioV on 04.09.18.
 */
@RequestScoped
public class DefaultQueryPredicateFactory implements QueryPredicateFactory {
    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager em;
    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<Item> itemQuery;
    private CriteriaQuery<Tag> tagQuery;
    private Root<Item> itemRoot;
    private Root<Tag> tagRoot;
    private ListJoin<Item, Tag> tagJoin;

    @PostConstruct
    public void init(){
        criteriaBuilder = em.getCriteriaBuilder();
        itemQuery = criteriaBuilder.createQuery(Item.class);
        tagQuery = criteriaBuilder.createQuery(Tag.class);
        itemRoot = itemQuery.from(Item.class);
        tagRoot = tagQuery.from(Tag.class);
        tagJoin = itemRoot.join(Item_.tags, JoinType.LEFT);
    }

    @Override
    public CriteriaQuery<Item> makeItemCriteriaQuery(@NotNull Predicate predicate) {
        itemQuery.where(predicate);
        itemQuery.distinct(true);
        return itemQuery;
    }

    @Override
    public CriteriaQuery<Tag> makeTagCriteriaQuery(@NotNull Predicate predicate) {
        tagQuery.where(predicate);
        tagQuery.distinct(true);
        return tagQuery;
    }

    @Override
    public Predicate makeItemNameLikePredicate(@NotBlank String itemName) {
        return criteriaBuilder.and(
                criteriaBuilder.like(itemRoot.get(Item_.name), makeLikePattern(itemName)),
                criteriaBuilder.isNotNull(itemRoot.get(Item_.name)));
    }

    @Override
    public Predicate makeItemOwnerLikePredicate(@NotBlank String owner) {
        return criteriaBuilder.and( 
                criteriaBuilder.like(itemRoot.get(Item_.owner), makeLikePattern(owner)),
                criteriaBuilder.isNotNull(itemRoot.get(Item_.owner)));
    }

    @Override
    public Predicate makeTagNameLikePredicate(@NotBlank String tagName) {
        return criteriaBuilder.and( 
                criteriaBuilder.like(tagRoot.get(Tag_.name), makeLikePattern(tagName)),
                criteriaBuilder.isNotNull(tagRoot.get(Tag_.name)));
    }

    @Override
    public Predicate makeItemTagLikePredicate(@NotBlank String tagName) {
        return criteriaBuilder.like(itemRoot.join(Item_.tags).get(Tag_.name), makeLikePattern(tagName));
    }

    @Override
    public Predicate makeItemTagLikePredicate(@NotNull Collection<@NotBlank String> tagNames) {
        ArrayList<Predicate> predicates = new ArrayList<>();
        for (String tagName : tagNames) {
            predicates.add(makeItemTagLikePredicate(tagName));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public Predicate makeItemOwnerEqualPredicate(@NotBlank String owner) {
        return criteriaBuilder.and( 
                criteriaBuilder.equal(itemRoot.get(Item_.owner), owner),
                criteriaBuilder.isNotNull(itemRoot.get(Item_.owner)));
    }

    @Override
    public Predicate makeItemNameEqualPredicate(@NotBlank String itemName) {
        return criteriaBuilder.and( 
                criteriaBuilder.equal(itemRoot.get(Item_.name), itemName),
                criteriaBuilder.isNotNull(itemRoot.get(Item_.name)));
    }

    @Override
    public Predicate makeItemTagEqualPredicate(@NotBlank String tagName) {
        return criteriaBuilder.equal(itemRoot.join(Item_.tags).get(Tag_.name), tagName);
    }

    @Override
    public Predicate makeItemTagEqualPredicate(@NotNull Collection<@NotBlank String> tagNames) {
        ArrayList<Predicate> predicates = new ArrayList<>();
        for (String tagName : tagNames) {
            predicates.add(makeItemTagEqualPredicate(tagName));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public Predicate makeTagNameEqualPredicate(@NotBlank String tagName) {
        return criteriaBuilder.and( 
                criteriaBuilder.equal(tagRoot.get(Tag_.name), tagName),
                criteriaBuilder.isNotNull(tagRoot.get(Tag_.name)));
    }

    @Override
    public Predicate makeItemOwnerInPredicate(@NotNull Collection<@NotBlank String> owners) {
        return criteriaBuilder.and( 
                itemRoot.get(Item_.owner).in(owners),
                criteriaBuilder.isNotNull(itemRoot.get(Item_.owner)));
    }

    @Override
    public Predicate makeItemNameInPredicate(@NotNull Collection<@NotBlank String> itemNames) {
        return criteriaBuilder.and( 
                itemRoot.get(Item_.name).in(itemNames),
                criteriaBuilder.isNotNull(itemRoot.get(Item_.name)));
    }

    @Override
    public Predicate makeTagNameInPredicate(@NotNull Collection<@NotBlank String> tagNames) {
        return criteriaBuilder.and(
                tagRoot.get(Tag_.name).in(tagNames),
                criteriaBuilder.isNotNull(tagRoot.get(Tag_.name)));
    }

    @Override
    public Predicate makeItemCreationDateBetweenPredicate(@NotNull Date from, @NotNull Date to) {
        return criteriaBuilder.and( 
                criteriaBuilder.between(itemRoot.get(Item_.creationDate), from, to),
                criteriaBuilder.isNotNull(itemRoot.get(Item_.creationDate)));
    }

    @Override
    public Predicate makeItemSharedIsTruePredicate() {
        return criteriaBuilder.isTrue(itemRoot.get(Item_.shared));
    }

    @Override
    public Predicate makeItemTypeEqualPredicate(@NotBlank String itemType) {
        return criteriaBuilder.and( 
                criteriaBuilder.equal(itemRoot.get(Item_.dType), itemType),
                criteriaBuilder.isNotNull(itemRoot.get(Item_.dType)));
    }

    @Override
    public Predicate makeConjunctionPredicate(@NotNull Collection<@NotNull Predicate> predicates) {
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
    
    @Override
    public Predicate makeConjunctionPredicate(@NotNull Predicate predicate1, @NotNull Predicate predicate2) {
        return criteriaBuilder.and(predicate1, predicate2);
    }

    @Override
    public Predicate makeDisjunctionPredicate(@NotNull Collection<@NotNull Predicate> predicates) {
        return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
    }

    @Override
    public Predicate makeDisjunctionPredicate(@NotNull Predicate predicate1, @NotNull Predicate predicate2) {
        return criteriaBuilder.or(predicate1, predicate2);
    }

    @Override
    public Predicate makeInversionPredicate(@NotNull Predicate predicate) {
        return criteriaBuilder.not(predicate);
    }

    private String makeLikePattern(String s) {
        if (s.startsWith("'") ) {
            s = s.substring(1);
        } else {
            s = "%" + s;
        }
        if (s.endsWith("'")) {
            return s.substring(0, s.length() - 1);
        } else {
            return s + "%";
        }
    }

    // Setters & Getters

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public void setCriteriaBuilder(CriteriaBuilder criteriaBuilder) {
        this.criteriaBuilder = criteriaBuilder;
    }

    public void setItemRoot(Root<Item> itemRoot) {
        this.itemRoot = itemRoot;
    }

    public void setTagRoot(Root<Tag> tagRoot) {
        this.tagRoot = tagRoot;
    }

    public void setTagJoin(ListJoin<Item, Tag> tagJoin) {
        this.tagJoin = tagJoin;
    }

    public EntityManager getEm() {
        return em;
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return criteriaBuilder;
    }

    public Root<Item> getItemRoot() {
        return itemRoot;
    }

    public Root<Tag> getTagRoot() {
        return tagRoot;
    }

    public ListJoin<Item, Tag> getTagJoin() {
        return tagJoin;
    }
}
