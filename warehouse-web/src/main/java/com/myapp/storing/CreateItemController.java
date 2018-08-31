package com.myapp.storing;

import org.primefaces.model.DualListModel;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
    @Inject
    private UploadFilesCollector uploadFilesCollector;
    private Principal principal;
    private Item item = new Item();
    private TextItem textItem = new TextItem();
    private FileItem fileItem = new FileItem();
    private List<String> newTagNames;
    private DualListModel<String> existingTagNamesDualListModel;

    @PostConstruct
    public void init() {
        principal = facesContext.getExternalContext().getUserPrincipal();
        existingTagNamesDualListModel = new DualListModel<>(fetchExistingTagNames(), new ArrayList<>());
    }

    private List<String> fetchExistingTagNames() {
        return tagStore.fetchTagNames();
    }

    public String createTextItem() throws IOException {
        createItem(textItem);
        return "/public/show-item?id=" + textItem.getId() + "&faces-redirect=true";
    }

    public void createFileItems() {
        while (!uploadFilesCollector.getTemporalFileItems().isEmpty()) {
            FileItem temporalFileItem = uploadFilesCollector.getTemporalFileItems().poll();
            if (temporalFileItem == null) {
                continue;
            }

            temporalFileItem.setName(fileItem.getName());
            temporalFileItem.setDescription(fileItem.getDescription());
            temporalFileItem.setShared(fileItem.isShared());

            createItem(temporalFileItem);

            facesContext.addMessage(null, new FacesMessage("FileItem \"" +
                    temporalFileItem.getName() + "\" id:" + temporalFileItem.getId() +
                    " was created successfully!"));
        }
    }

    private void createItem(Item item) {
        if (principal != null) {
            item.setOwner(principal.getName());
        }
        Date date = Date.from(Instant.now().minus(1, ChronoUnit.SECONDS));
        item.setCreationDate(date);

        itemStore.saveItems(item);
        if (newTagNames != null) {
            Set<String> tagNamesSet = new HashSet<>(newTagNames);
            tagNamesSet.addAll(existingTagNamesDualListModel.getTarget());

            for (String tag : tagNamesSet) {
                tagStore.saveTag(tag, item);
            }
        }
    }

    public List<String> autocompleteTags(String query) {
        return tagStore.fetchTagsNameStartsWith(query).stream().map(Tag::getName).collect(Collectors.toList());
    }

    //Getters & Setters

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

    public FacesContext getFacesContext() {
        return facesContext;
    }

    public void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
    }

    public FileItem getFileItem() {
        return fileItem;
    }

    public void setFileItem(FileItem fileItem) {
        this.fileItem = fileItem;
    }

    public List<String> getNewTagNames() {
        return newTagNames;
    }

    public void setNewTagNames(List<String> newTagNames) {
        this.newTagNames = newTagNames;
    }

    public DualListModel<String> getExistingTagNamesDualListModel() {
        return existingTagNamesDualListModel;
    }

    public void setExistingTagNamesDualListModel(DualListModel<String> existingTagNamesDualListModel) {
        this.existingTagNamesDualListModel = existingTagNamesDualListModel;
    }
}
