package com.myapp.storing;

import javax.persistence.criteria.CriteriaQuery;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * <p>Created by MontolioV on 04.07.18.
 */
public interface ItemStore {

    List<Item> getTenLastSharedItems();

    Item getItemById(long id, String userName);

    Set<String> getHashesOfFileItems();

    void saveItems(Item... items);

    void deleteAnyItem(long id);

    int deleteAllItems();

    void deleteItemByOwner(long id, @NotNull String userName);

    void deleteOldItemsWithNoOwner(Instant cutoffInstant);

    List<Item> executeCustomSelectQuery(CriteriaQuery<Item> criteriaQuery);
}
