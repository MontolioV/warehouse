package com.myapp.storing;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * <p>Created by MontolioV on 04.07.18.
 */
public interface TagStore {
    
    void saveTag(String tagName, Item... items);

    List<Tag> executeCustomSelectQuery(CriteriaQuery<Tag> criteriaQuery);

    List<Tag> fetchMostPopularTags(@Positive int amount);

    List<Tag> fetchTagsNameContains(@NotBlank String string);

    List<Tag> fetchTagsNameStartsWith(@NotBlank String string);

    List<Tag> fetchTagsNameEndsWith(@NotBlank String string);

    List<String> fetchTagNames();
}
