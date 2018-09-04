package com.myapp.storing;

import javax.persistence.criteria.Predicate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;

/**
 * <p>Created by MontolioV on 03.09.18.
 */
public interface QueryPredicateFactory {

    Predicate makeItemNameLikePredicate(@NotBlank String itemName);

    Predicate makeItemOwnerLikePredicate(@NotBlank String owner);

    Predicate makeTagNameLikePredicate(@NotBlank String tagName);

    Predicate makeItemJoinTagNameLikePredicate(@NotBlank String tagName);

    Predicate makeItemOwnerInPredicate(@NotNull Collection<@NotBlank String> owners);

    Predicate makeItemNameInPredicate(@NotNull Collection<@NotBlank String> itemNames);

    Predicate makeItemJoinTagNameInPredicate(@NotNull Collection<@NotBlank String> tagNames);

    Predicate makeTagNameInPredicate(@NotNull Collection<@NotBlank String> tagNames);

    Predicate makeItemCreationDateBetweenPredicate(@NotNull Date from, @NotNull Date to);

    Predicate makeItemSharedIsTruePredicate();

    Predicate makeItemTypeEqualPredicate(@NotBlank String itemType);

    Predicate makeConjunctionPredicate(@NotNull Collection<@NotNull Predicate> predicates);

    Predicate makeConjunctionPredicate(@NotNull Predicate predicate1, @NotNull Predicate predicate2);

    Predicate makeDisjunctionPredicate(@NotNull Collection<@NotNull Predicate> predicates);

    Predicate makeDisjunctionPredicate(@NotNull Predicate predicate1, @NotNull Predicate predicate2);

    Predicate makeInversionPredicate(@NotNull Predicate predicate);
}
