package com.myapp.storing;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.myapp.security.Roles.Const.ADMIN;
import static com.myapp.security.Roles.Const.MODERATOR;

/**
 * <p>Created by MontolioV on 17.04.18.
 */
@Stateless
public class ItemStore {
    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager em;
    @Resource
    private SessionContext sessionContext;

    public List<Item> getTenLastSharedItems() {
        return em.createNamedQuery(Item.GET_LAST_SHARED, Item.class).setMaxResults(10).getResultList();
    }

    public Item getItemById(long id, String userName) {
        Item item = em.find(Item.class, id);
        if (item != null && (item.isShared() || item.getOwner().equals(userName))) {
            return item;
        } else {
            return null;
        }
    }

    public void saveItems(Item... items) {
        for (Item item : items) {
            em.persist(item);
        }
    }

    @RolesAllowed(MODERATOR)
    public void deleteAnyItem(long id) {
        deleteItem(id);
    }

    @RolesAllowed(ADMIN)
    public int deleteAllItems() {
        List<Item> resultList = em.createNamedQuery(Item.GET_ALL, Item.class).getResultList();
        resultList.forEach(em::remove);
        return resultList.size();
    }

    private void deleteItem(long id) {
        try {
            Item item = em.find(Item.class, id);
            em.remove(item);
        } catch (IllegalArgumentException e) {
            //no item, no cry
        }
    }

    public List<Item> customSelectQuery(String nameContains,
                                      List<String> owners,
                                      List<Tag> tags,
                                      boolean tagConjunction)
    {

    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public SessionContext getSessionContext() {
        return sessionContext;
    }

    public void setSessionContext(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }
}
