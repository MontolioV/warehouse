package com.myapp.storing;

import org.primefaces.model.DualListModel;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.Part;
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
    private FileStore fileStore;
    @EJB
    private TagStore tagStore;
    private Principal principal;
    private Item item = new Item();
    private TextItem textItem = new TextItem();
    private FileItem fileItem = new FileItem();
    private Part tmpFile;
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

    // TODO: 15.05.18 change redirects
    public void createTextItem() throws IOException {
        createItem(textItem);
        facesContext.getExternalContext().redirect(facesContext.getExternalContext().getApplicationContextPath());
    }

    public void createFileItem() throws IOException {
        if (tmpFile == null) {
            return;
        }

        if (tmpFile.getSize() > FileItem.MAX_SIZE_BYTE) {
            facesContext.addMessage("fileInput", new FacesMessage("File is too large!"));
            return;
        }

        try {
            String hash = fileStore.persistFile(tmpFile);
            fileItem.setHash(hash);
        } catch (IOException e) {
            e.printStackTrace();
            facesContext.addMessage("fileInput", new FacesMessage("File download fail. Try again."));
            return;
        }

        fileItem.setContentType(tmpFile.getContentType());
        fileItem.setNativeName(tmpFile.getSubmittedFileName());
        fileItem.setSize(tmpFile.getSize());

        createItem(fileItem);
        facesContext.getExternalContext().redirect(facesContext.getExternalContext().getApplicationContextPath());
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
        return tagStore.fetchTagsLikeName(query).stream().map(Tag::getName).collect(Collectors.toList());
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

    public Part getTmpFile() {
        return tmpFile;
    }

    public void setTmpFile(Part tmpFile) {
        this.tmpFile = tmpFile;
    }

    public FileStore getFileStore() {
        return fileStore;
    }

    public void setFileStore(FileStore fileStore) {
        this.fileStore = fileStore;
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
