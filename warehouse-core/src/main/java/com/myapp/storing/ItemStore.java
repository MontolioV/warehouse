package com.myapp.storing;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

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

    public List<Item> getTenLastItems() {
        return em.createNamedQuery(Item.GET_LAST_SHARED, Item.class).setMaxResults(10).getResultList();
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

    private void deleteItem(long id) {
        try {
            Item item = em.find(Item.class, id);
            em.remove(item);
        } catch (IllegalArgumentException e) {
            //no item, no cry
        }
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }
}
