package com.myapp.storing;

import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;

import static com.myapp.security.Roles.Const.MODERATOR;

/**
 * <p>Created by MontolioV on 18.04.18.
 */
@Model
public class DeleteItemController {
    @Inject
    private FacesContext facesContext;
    @EJB
    private ItemStore itemStore;
    @EJB
    private FileStoreCleaner fileStoreCleaner;
    private Long id;

    // TODO: 23.08.18 Messages
    public void deleteByIDNoRedirect() {
        ExternalContext externalContext = facesContext.getExternalContext();
        if (id == null || externalContext.getUserPrincipal() == null) {
            return;
        } else if (externalContext.isUserInRole(MODERATOR)) {
            itemStore.deleteAnyItem(id);
        } else {
            String userName = externalContext.getUserPrincipal().getName();
            itemStore.deleteItemByOwner(id, userName);
        }
    }

    public void deleteByIDAndGoHome() throws IOException {
        deleteByIDNoRedirect();
        facesContext.getExternalContext().redirect(facesContext.getExternalContext().getApplicationContextPath());
    }

    public void deleteAll() {
        int deletedItems = itemStore.deleteAllItems();
        facesContext.addMessage(null, new FacesMessage(deletedItems + " items deleted!"));
    }

    public void cleanupFileStorage() {
        long bytesCleanedUp = fileStoreCleaner.cleanup();
        double gbCleanedUp = bytesCleanedUp / Math.pow(10, 9);
        facesContext.addMessage(null, new FacesMessage(String.format("%f GB freed!", gbCleanedUp)));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemStore getItemStore() {
        return itemStore;
    }

    public void setItemStore(ItemStore itemStore) {
        this.itemStore = itemStore;
    }
}
