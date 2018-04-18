package com.myapp.storing;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import java.util.List;

/**
 * <p>Created by MontolioV on 18.04.18.
 */
@Model
public class FetchItemsController {
    @EJB
    private ItemStore itemStore;
    private List<Item> items;

    @PostConstruct
    public void init() {
        fetchRecentItems();
    }

    public void fetchRecentItems() {
        items = itemStore.getTenLastItems();
    }

    public ItemStore getItemStore() {
        return itemStore;
    }

    public void setItemStore(ItemStore itemStore) {
        this.itemStore = itemStore;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
