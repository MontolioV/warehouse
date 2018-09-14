package com.myapp.storing;

import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>Created by MontolioV on 07.05.18.
 */
@Model
public class DownloadItemController {
    @Inject
    private FacesContext facesContext;
    @EJB
    private ItemStore itemStore;
    @EJB
    private FileStore fileStore;
    private long id;

    public void downloadAndSaveItem() throws IOException {
        ExternalContext ec = facesContext.getExternalContext();
        FileItem item = (FileItem) itemStore.getItemById(id);

        ec.responseReset();
        ec.setResponseContentType(item.getContentType());
        ec.setResponseContentLength((int) item.getSize());
        ec.addResponseHeader("Content-Disposition", "attachment; filename=\"" + item.getNativeName() + "\"");
        try (OutputStream os = ec.getResponseOutputStream()) {
            fileStore.uploadFile(item.getHash(), os);
        }

        facesContext.responseComplete();
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
