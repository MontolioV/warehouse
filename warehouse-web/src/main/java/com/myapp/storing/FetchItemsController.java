package com.myapp.storing;

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
import java.util.stream.Collectors;

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
    private TagStore tagStore;
    @EJB
    private ItemTagLikeQueryBuilder likeQueryBuilder;
    private List<Item> items = new ArrayList<>();
    private Long id;
    private Item item;
    private TextItem textItem;
    private FileItem fileItem;
    private List<String> itemNames;
    private List<String> itemOwners;
    private List<String> tags;
    private boolean tagsConjunction = true;

    @PostConstruct
    public void init() {
        if (externalContext.getUserPrincipal() != null) {
            itemOwners = new ArrayList<>();
            itemOwners.add("\"" + externalContext.getUserPrincipal().getName() + "\"");
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

    public void castItem() {
        if (item == null) {
            return;
        }

        if (item.getdType().equals(TextItem.class.getSimpleName())) {
            textItem = (TextItem) item;
        }else if (item.getdType().equals(FileItem.class.getSimpleName())) {
            fileItem = (FileItem) item;
        }
    }

    public void filteredFetch() {
        if (itemNames != null && !itemNames.isEmpty()) {
            likeQueryBuilder.selectPredicateTarget(QueryTarget.ITEM_NAME);
            likeQuerySequence(itemNames);
        }
        if (itemOwners != null && !itemOwners.isEmpty()) {
            likeQueryBuilder.selectPredicateTarget(QueryTarget.ITEM_OWNER);
            likeQuerySequence(itemOwners);
        }
        if (tags != null && !tags.isEmpty()) {
            likeQueryBuilder.selectPredicateTarget(QueryTarget.ITEM_JOIN_TAG_NAME);
            likeQuerySequence(tags);
        }

        CriteriaQuery<Item> itemCriteriaQuery = likeQueryBuilder.constructItemQuery();
        if (itemCriteriaQuery != null) {
            items = itemStore.executeCustomSelectQuery(itemCriteriaQuery);

            if (tagsConjunction && tags != null && !tags.isEmpty()) {
                ensureTagConjunction();
            }
        }
    }

    private void likeQuerySequence(List<String> strings) {
        likeQueryBuilder.constructLikePredicates(strings.toArray(new String[0]));
        likeQueryBuilder.generateWherePredicates(false);
    }

    private void ensureTagConjunction() {
        likeQueryBuilder.selectPredicateTarget(QueryTarget.TAG_NAME);
        likeQueryBuilder.constructLikePredicates(tags.toArray(new String[0]));
        likeQueryBuilder.generateWherePredicates(false);
        List<Tag> tags = tagStore.executeCustomSelectQuery(likeQueryBuilder.constructTagQuery());

        // TODO: 30.04.2018 Primefaces chips don't pass "1" after pressed enter
        items = items.stream()
                .filter(item1 -> item1.getTags().size() >= tags.size())
                .filter(item1 -> item1.getTags().containsAll(tags))
                .collect(Collectors.toList());
    }

    public boolean fileIsImage() {
        if (fileItem != null && fileItem.getContentType().startsWith("image")) {
            return true;
        }
        return false;
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

    public ItemTagLikeQueryBuilder getLikeQueryBuilder() {
        return likeQueryBuilder;
    }

    public void setLikeQueryBuilder(ItemTagLikeQueryBuilder likeQueryBuilder) {
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

    public boolean isTagsConjunction() {
        return tagsConjunction;
    }

    public void setTagsConjunction(boolean tagsConjunction) {
        this.tagsConjunction = tagsConjunction;
    }

    public TagStore getTagStore() {
        return tagStore;
    }

    public void setTagStore(TagStore tagStore) {
        this.tagStore = tagStore;
    }

    public TextItem getTextItem() {
        return textItem;
    }

    public void setTextItem(TextItem textItem) {
        this.textItem = textItem;
    }

    public FileItem getFileItem() {
        return fileItem;
    }

    public void setFileItem(FileItem fileItem) {
        this.fileItem = fileItem;
    }
}