package com.myapp.storing;

import javax.ejb.EJB;
import javax.enterprise.inject.Model;

/**
 * <p>Created by MontolioV on 18.04.18.
 */
@Model
public class DeleteItemController {
    @EJB
    private ItemStore itemStore;
    private Long id;

    public void deleteByID() {
        itemStore.deleteAnyItem(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemStore getItemStore() {
        return itemStore;
    }

    public void setItemStore(ItemStore itemStore) {
        this.itemStore = itemStore;
    }
}
