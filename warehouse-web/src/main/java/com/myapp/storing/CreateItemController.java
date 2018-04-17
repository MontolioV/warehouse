package com.myapp.storing;

import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.Arrays;

/**
 * <p>Created by MontolioV on 17.04.18.
 */
@Model
public class CreateItemController {
    @Inject
    private FacesContext facesContext;
    @EJB
    private ItemStore itemStore;
    @EJB
    private TagStore tagStore;
    private Item item;
    private TextItem textItem;
    private String tagsString;

    public String createTextItem() {
        itemStore.saveItems(textItem);
        for (String tag : parseTags()) {
            tagStore.saveTag(tag, textItem);
        }
        return "index?faces-redirect=true";
    }

    private String[] parseTags() {
        return Arrays.stream(tagsString.split("\n"))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    public FacesContext getFacesContext() {
        return facesContext;
    }

    public void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
    }

    public ItemStore getItemStore() {
        return itemStore;
    }

    public void setItemStore(ItemStore itemStore) {
        this.itemStore = itemStore;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public TextItem getTextItem() {
        return textItem;
    }

    public void setTextItem(TextItem textItem) {
        this.textItem = textItem;
    }

    public String getTagsString() {
        return tagsString;
    }

    public void setTagsString(String tagsString) {
        this.tagsString = tagsString;
    }
}
