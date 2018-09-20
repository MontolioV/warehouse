package com.myapp.storing;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by MontolioV on 18.04.18.
 */
@Model
public class FetchItemsController {
    @Resource(lookup = "java:/strings/webAppAddress")
    private String webAppAddress;
    @Inject
    private ExternalContext externalContext;
    @EJB
    private ItemStore itemStore;
    private List<Item> recentItems = new ArrayList<>();
    private long id;
    private Item item;
    private TextItem textItem;
    private FileItem fileItem;

    public void fetchRecentItems() {
        recentItems = itemStore.getTenLastSharedItems();
    }

    public String fetchById() {
        item = itemStore.getItemById(id);
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

    public boolean fileIsImage() {
        if (fileItem != null && fileItem.getContentType().startsWith("image")) {
            return true;
        }
        return false;
    }

    public boolean itemIsUsersOwn() {
        Principal userPrincipal = externalContext.getUserPrincipal();
        return item != null
                && userPrincipal != null
                && item.getOwner() != null
                && item.getOwner().equals(userPrincipal.getName());
    }

    public String sanitisedText() {
        if (textItem == null) {
            return null;
        }
        String text = textItem.getText();
        return Jsoup.clean(text, Whitelist.relaxed());
    }

    public String getLinkToItem() {
        return webAppAddress + "public/show-item.jsf?id=" + id;
    }

    //Getters & Setters

    public String getWebAppAddress() {
        return webAppAddress;
    }

    public void setWebAppAddress(String webAppAddress) {
        this.webAppAddress = webAppAddress;
    }

    public List<Item> getRecentItems() {
        return recentItems;
    }

    public void setRecentItems(List<Item> recentItems) {
        this.recentItems = recentItems;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public FileItem getFileItem() {
        return fileItem;
    }

    public void setFileItem(FileItem fileItem) {
        this.fileItem = fileItem;
    }
}
