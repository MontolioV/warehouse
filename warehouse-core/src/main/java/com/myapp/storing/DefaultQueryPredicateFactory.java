package com.myapp.storing;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;

/**
 * <p>Created by MontolioV on 04.09.18.
 */
@ApplicationScoped
public class DefaultQueryPredicateFactory implements QueryPredicateFactory {
    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager em;
    private CriteriaBuilder criteriaBuilder;
    private Root<Item> itemRoot;
    private Root<Tag> tagRoot;
    private ListJoin<Item, Tag> tagJoin;

    @PostConstruct
    public void init(){
        criteriaBuilder = em.getCriteriaBuilder();
        itemRoot = criteriaBuilder.createQuery().from(Item.class);
        tagRoot = criteriaBuilder.createQuery().from(Tag.class);
        tagJoin = itemRoot.join(Item_.tags, JoinType.LEFT);
    }

    @Override
    public Predicate makeItemNameLikePredicate(@NotBlank String itemName) {
        return criteriaBuilder.like(itemRoot.get(Item_.name), makeLikePattern(itemName));
    }

    @Override
    public Predicate makeItemOwnerLikePredicate(@NotBlank String owner) {
        return criteriaBuilder.like(itemRoot.get(Item_.owner), makeLikePattern(owner));
    }

    @Override
    public Predicate makeTagNameLikePredicate(@NotBlank String tagName) {
        return criteriaBuilder.like(tagRoot.get(Tag_.name), makeLikePattern(tagName));
    }

    @Override
    public Predicate makeItemJoinTagNameLikePredicate(@NotBlank String tagName) {
        return criteriaBuilder.like(tagJoin.get(Tag_.name), makeLikePattern(tagName));
    }

    @Override
    public Predicate makeItemOwnerInPredicate(@NotNull Collection<@NotBlank String> owners) {
        return itemRoot.get(Item_.owner).in(owners);
    }

    @Override
    public Predicate makeItemNameInPredicate(@NotNull Collection<@NotBlank String> itemNames) {
        return itemRoot.get(Item_.name).in(itemNames);
    }

    @Override
    public Predicate makeItemJoinTagNameInPredicate(@NotNull Collection<@NotBlank String> tagNames) {
        return tagJoin.get(Tag_.name).in(tagNames);
    }

    @Override
    public Predicate makeTagNameInPredicate(@NotNull Collection<@NotBlank String> tagNames) {
        return tagRoot.get(Tag_.name).in(tagNames);
    }

    @Override
    public Predicate makeItemCreationDateBetweenPredicate(@NotNull Date from, @NotNull Date to) {
        return criteriaBuilder.between(itemRoot.get(Item_.creationDate), from, to);
    }

    @Override
    public Predicate makeItemSharedIsTruePredicate() {
        return criteriaBuilder.isTrue(itemRoot.get(Item_.shared));
    }

    @Override
    public Predicate makeItemTypeEqualPredicate(@NotBlank String itemType) {
        return criteriaBuilder.equal(itemRoot.get(Item_.dType), itemType);
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
