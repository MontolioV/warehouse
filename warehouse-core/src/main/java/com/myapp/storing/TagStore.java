package com.myapp.storing;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * <p>Created by MontolioV on 17.04.18.
 */
@Stateless
public class TagStore {
    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager em;

    public void saveTag(String tagName, Item... items) {
        List<Tag> resultList = em.createNamedQuery(Tag.GET_BY_NAME, Tag.class)
                .setParameter("name", tagName)
                .getResultList();
        if (resultList.isEmpty()) {
            Tag tag = new Tag();
            tag.setName(tagName);
            bind(tag, items);
            em.persist(tag);
        } else {
            bind(resultList.get(0), items);
        }
    }

    private void bind(Tag tag, Item... items) {
        for (Item item : items) {
            em.find(Item.class, item.getId()).getTags().add(tag);
            tag.getItems().add(item);
        }
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }
}
