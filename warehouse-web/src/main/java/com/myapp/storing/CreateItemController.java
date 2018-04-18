package com.myapp.storing;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Date;

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
    private Principal principal;
    private Item item = new Item();
    private TextItem textItem = new TextItem();
    private String tagsString;

    @PostConstruct
    public void init() {
        principal = facesContext.getExternalContext().getUserPrincipal();
    }

    public void createTextItem() throws IOException {
        if (principal != null) {
            textItem.setOwner(principal.getName());
        }
        textItem.setCreationDate(new Date());

        itemStore.saveItems(textItem);
        for (String tag : parseTags()) {
            tagStore.saveTag(tag, textItem);
        }

        facesContext.getExternalContext().redirect(facesContext.getExternalContext().getApplicationContextPath());
    }

    private String[] parseTags() {
        return Arrays.stream(tagsString.split("\n"))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    public TagStore getTagStore() {
        return tagStore;
    }

    public void setTagStore(TagStore tagStore) {
        this.tagStore = tagStore;
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
