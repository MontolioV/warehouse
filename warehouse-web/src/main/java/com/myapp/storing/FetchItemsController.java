package com.myapp.storing;

import com.myapp.utils.LikeQueryBuilder;
import com.myapp.utils.QueryTarget;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaQuery;
import java.security.Principal;
import java.util.ArrayList;
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
    @EJB
    private LikeQueryBuilder likeQueryBuilder;
    private List<Item> items;
    private Long id;
    private Item item;
    private List<String> itemNames;
    private List<String> itemOwners;
    private List<String> tags;
    private boolean itemNamesConjunction = false;
    private boolean itemOwnersConjunction = false;
    private boolean tagsConjunction = true;

    @PostConstruct
    public void init() {
        String name = externalContext.getUserPrincipal().getName();
        if (name != null) {
            itemOwners = new ArrayList<>();
            itemOwners.add(name);
        }
    }

    public void fetchRecentItems() {
        items = itemStore.getTenLastSharedItems();
    }

    public String fetchById() {
        Principal userPrincipal = externalContext.getUserPrincipal();
        String username = null;
        if (userPrincipal != null) {
            username = userPrincipal.getName();
        }

        item = itemStore.getItemById(id, username);
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

    public void filteredFetch() {
        if (itemNames != null && !itemNames.isEmpty()) {
            likeQueryBuilder.selectPredicateTarget(QueryTarget.ITEM_NAME);
            likeQuerySequence(itemNames, itemNamesConjunction);
        }
        if (itemOwners != null && !itemOwners.isEmpty()) {
            likeQueryBuilder.selectPredicateTarget(QueryTarget.ITEM_OWNER);
            likeQuerySequence(itemOwners, itemOwnersConjunction);
        }
        if (tags != null && !tags.isEmpty()) {
            likeQueryBuilder.selectPredicateTarget(QueryTarget.TAG_NAME);
            likeQuerySequence(tags, tagsConjunction);
        }

        CriteriaQuery<Item> itemCriteriaQuery = likeQueryBuilder.constructQuery();
        if (itemCriteriaQuery != null) {
            items = itemStore.executeCustomSelectQuery(itemCriteriaQuery);
        }
    }

    private void likeQuerySequence(List<String> strings, boolean conjunction) {
        likeQueryBuilder.constructLikePredicates(strings.toArray(new String[0]));
        likeQueryBuilder.generateWherePredicates(conjunction);
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

    public LikeQueryBuilder getLikeQueryBuilder() {
        return likeQueryBuilder;
    }

    public void setLikeQueryBuilder(LikeQueryBuilder likeQueryBuilder) {
        this.likeQueryBuilder = likeQueryBuilder;
    }

    public List<String> getItemNames() {
        return itemNames;
    }

    public void setItemNames(List<String> itemNames) {
        this.itemNames = itemNames;
    }

    public List<String> getItemOwners() {
        return itemOwners;
    }

    public void setItemOwners(List<String> itemOwners) {
        this.itemOwners = itemOwners;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isItemNamesConjunction() {
        return itemNamesConjunction;
    }

    public void setItemNamesConjunction(boolean itemNamesConjunction) {
        this.itemNamesConjunction = itemNamesConjunction;
    }

    public boolean isItemOwnersConjunction() {
        return itemOwnersConjunction;
    }

    public void setItemOwnersConjunction(boolean itemOwnersConjunction) {
        this.itemOwnersConjunction = itemOwnersConjunction;
    }

    public boolean isTagsConjunction() {
        return tagsConjunction;
    }

    public void setTagsConjunction(boolean tagsConjunction) {
        this.tagsConjunction = tagsConjunction;
    }
}
