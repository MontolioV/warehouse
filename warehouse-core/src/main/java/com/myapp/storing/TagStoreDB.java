package com.myapp.storing;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

import static com.myapp.storing.Tag.NAME_PARAM;

/**
 * <p>Created by MontolioV on 17.04.18.
 */
@Stateless
public class TagStoreDB implements TagStore{
    @PersistenceContext(unitName = "warehouse-api-pu")
    private EntityManager em;
    @Resource
    private SessionContext sessionContext;

    @Override
    public void saveTag(String tagName, Item... items) {
        String tagNameFormatted = tagName.trim();
        List<Tag> resultList = em.createNamedQuery(Tag.GET_BY_NAME, Tag.class)
                .setParameter(NAME_PARAM, tagNameFormatted)
                .getResultList();
        if (resultList.isEmpty()) {
            Tag tag = new Tag();
            tag.setName(tagNameFormatted);
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
        tag.updateLazyItemCounter();
    }

    @Override
    public List<Tag> executeCustomSelectQuery(CriteriaQuery<Tag> criteriaQuery) {
        return em.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<Tag> fetchMostPopularTags(@Positive int amount) {
        List<Tag> resultList = em.createNamedQuery(Tag.GET_MOST_POPULAR, Tag.class)
                .setMaxResults(amount)
                .getResultList();
        return resultList;
    }

    @Override
    public List<Tag> fetchTagsNameContains(@NotBlank String string) {
        return em.createNamedQuery(Tag.GET_LIKE_NAME, Tag.class)
                .setParameter(Tag.NAME_PARAM, "%" + string + "%")
                .getResultList();
    }

    @Override
    public List<Tag> fetchTagsNameStartsWith(@NotBlank String string) {
        return em.createNamedQuery(Tag.GET_LIKE_NAME, Tag.class)
                .setParameter(Tag.NAME_PARAM, "%" + string)
                .getResultList();
    }

    @Override
    public List<Tag> fetchTagsNameEndsWith(@NotBlank String string) {
        return em.createNamedQuery(Tag.GET_LIKE_NAME, Tag.class)
                .setParameter(Tag.NAME_PARAM, string + "%")
                .getResultList();
    }

    @Override
    public List<String> fetchTagNames() {
        return em.createNamedQuery(Tag.GET_ALL, Tag.class).getResultList().stream().map(Tag::getName).collect(Collectors.toList());
    }
}
