package com.myapp.storing;

import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * <p>Created by MontolioV on 04.07.18.
 */
public interface TagStore {
    
    void saveTag(String tagName, Item... items);

    List<Tag> executeCustomSelectQuery(CriteriaQuery<Tag> criteriaQuery);
}
