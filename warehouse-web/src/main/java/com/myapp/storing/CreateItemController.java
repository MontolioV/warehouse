package com.myapp.storing;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
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
    private FileItem fileItem = new FileItem();
    private Part tmpFile;
    private String tagsString;


    @PostConstruct
    public void init() {
        principal = facesContext.getExternalContext().getUserPrincipal();
    }

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

        fileItem.setContentType(tmpFile.getContentType());
        fileItem.setNativeName(tmpFile.getSubmittedFileName());
        fileItem.setSize(tmpFile.getSize());
        try (InputStream inputStream = tmpFile.getInputStream()) {
            byte[] bytes = new byte[(int) tmpFile.getSize()];
            inputStream.read(bytes);
            fileItem.setBinaryData(bytes);
        }

        createItem(fileItem);
        facesContext.getExternalContext().redirect(facesContext.getExternalContext().getApplicationContextPath());
    }

    private void createItem(Item item) {
        if (principal != null) {
            item.setOwner(principal.getName());
        }
        item.setCreationDate(new Date());

        itemStore.saveItems(item);
        for (String tag : parseTags()) {
            tagStore.saveTag(tag, item);
        }
    }

    private String[] parseTags() {
        String cleanStr = tagsString.replaceAll("[^\\w\\n]", "");
        return Arrays.stream(cleanStr.split("\n"))
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
}
