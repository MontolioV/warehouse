package com.myapp.storing;

import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * <p>Created by MontolioV on 04.07.18.
 */
public interface ItemStore {

    public List<Item> getTenLastSharedItems();

    public Item getItemById(long id, String userName);

    public void saveItems(Item... items);

    public void deleteAnyItem(long id);

    public int deleteAllItems();

    public List<Item> executeCustomSelectQuery(CriteriaQuery<Item> criteriaQuery);
}
