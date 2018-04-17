package com.myapp.storing;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * <p>Created by MontolioV on 17.04.18.
 */
@Stateless
public class ItemStore {
    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager em;

    public List<Item> getTenLastItems() {
        return em.createNamedQuery(Item.GET_LAST, Item.class).setMaxResults(10).getResultList();
    }

    public void saveItems(Item... items) {
        for (Item item : items) {
            em.persist(item);
        }
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }
}
