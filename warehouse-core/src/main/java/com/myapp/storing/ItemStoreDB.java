package com.myapp.storing;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.myapp.security.Roles.Const.*;

/**
 * <p>Created by MontolioV on 17.04.18.
 */
@Stateless
public class ItemStoreDB implements ItemStore{
    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager em;
    @Resource
    private SessionContext sessionContext;

    @Override
    public List<Item> getTenLastSharedItems() {
        return em.createNamedQuery(Item.GET_LAST_SHARED, Item.class).setMaxResults(10).getResultList();
    }

    @Override
    public List<Item> getAllAccessibleItems() {
        String owner = sessionContext.getCallerPrincipal().getName();
        return em.createNamedQuery(Item.GET_ALL_ACCESSIBLE, Item.class)
                .setParameter(Item.OWNER_PARAM, owner)
                .getResultList();
    }

    @Override
    public Item getItemById(long id, String userName) {
        Item item = em.find(Item.class, id);
        if (item != null && (item.isShared() || item.getOwner().equals(userName))) {
            return item;
        } else {
            return null;
        }
    }

    @Override
    public Set<String> getHashesOfFileItems() {
        List<String> resultList = em.createNamedQuery(FileItem.GET_ALL_HASHES, String.class).getResultList();
        return new HashSet<>(resultList);
    }

    @Override
    public void saveItems(Item... items) {
        for (Item item : items) {
            em.persist(item);
        }
    }

    @Override
    @RolesAllowed(MODERATOR)
    public void deleteAnyItem(long id) {
        deleteItem(id);
    }

    @Override
    @RolesAllowed(ADMIN)
    public int deleteAllItems() {
        List<Item> resultList = em.createNamedQuery(Item.GET_ALL, Item.class).getResultList();
        resultList.forEach(em::remove);
        return resultList.size();
    }

    @Override
    @RolesAllowed(USER)
    public void deleteItemByOwner(long id, @NotNull String userName) {
        Item item = em.find(Item.class, id);
        if (userName.equals(item.getOwner())) {
            deleteItem(id);
        }
    }

    @Override
    public void deleteOldItemsWithNoOwner(Instant cutoffInstant) {
        List<Item> resultList = em.createNamedQuery(Item.GET_EXPIRED, Item.class)
                .setParameter(Item.MINIMAL_CREATION_DATE_PARAM, Date.from(cutoffInstant))
                .getResultList();
        resultList.forEach(em::remove);
    }

    private void deleteItem(long id) {
        try {
            Item item = em.find(Item.class, id);
            em.remove(item);
        } catch (IllegalArgumentException e) {
            //no item, no cry
        }
    }

    @Override
    public List<Item> executeCustomSelectQuery(CriteriaQuery<Item> criteriaQuery) {
        return em.createQuery(criteriaQuery).getResultList();
    }
}
