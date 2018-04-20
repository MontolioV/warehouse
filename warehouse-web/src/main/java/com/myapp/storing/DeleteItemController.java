package com.myapp.storing;

import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 * <p>Created by MontolioV on 18.04.18.
 */
@Model
public class DeleteItemController {
    @Inject
    private FacesContext facesContext;
    @EJB
    private ItemStore itemStore;
    private Long id;

    public void deleteByID() {
        itemStore.deleteAnyItem(id);
    }

    public void deleteAll() {
        int deletedItems = itemStore.deleteAllItems();
        facesContext.addMessage(null, new FacesMessage(deletedItems + " items deleted!"));
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
