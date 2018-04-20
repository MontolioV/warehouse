package com.myapp.storing;

import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import java.util.List;

/**
 * <p>Created by MontolioV on 18.04.18.
 */
@Model
public class FetchItemsController {
    @Inject
    private ExternalContext externalContext;
    @EJB
    private ItemStore itemStore;
    private List<Item> items;
    private Long id;
    private Item item;

    public void fetchRecentItems() {
        items = itemStore.getTenLastSharedItems();
    }

    public String fetchById() {
        item = itemStore.getItemById(id, externalContext.getUserPrincipal().getName());
        if (item == null) {
            return "missing-item-error";
        } else {
            return null;
        }
    }

    public String retrieveTextFromTextItem() {
        TextItem textItem = (TextItem) item;
        return textItem.getText();
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public ExternalContext getExternalContext() {
        return externalContext;
    }

    public void setExternalContext(ExternalContext externalContext) {
        this.externalContext = externalContext;
    }
}
