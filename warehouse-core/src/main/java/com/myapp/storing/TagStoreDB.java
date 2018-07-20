package com.myapp.storing;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

import static com.myapp.storing.Tag.NAME_PARAM;

/**
 * <p>Created by MontolioV on 17.04.18.
 */
@Stateless
public class TagStoreDB implements TagStore{
    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager em;

    @Override
    public void saveTag(String tagName, Item... items) {
        List<Tag> resultList = em.createNamedQuery(Tag.GET_BY_NAME, Tag.class)
                .setParameter(NAME_PARAM, tagName)
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

    @Override
    public List<Tag> executeCustomSelectQuery(CriteriaQuery<Tag> criteriaQuery) {
        return em.createQuery(criteriaQuery).getResultList();
    }
}
