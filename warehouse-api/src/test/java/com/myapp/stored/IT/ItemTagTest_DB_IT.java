package com.myapp.stored.IT;

import com.myapp.WithEmbeddedDB;
import com.myapp.stored.Item;
import com.myapp.stored.Tag;
import org.junit.Test;

import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.myapp.TestUtils.showConstraintViolations;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * <p>Created by MontolioV on 16.04.18.
 */
public class ItemTagTest_DB_IT extends WithEmbeddedDB {
    public static final String TEST_1 = "TEST_1"; 
    public static final String TEST_2 = "TEST_2"; 
    public static final String TEST_3 = "TEST_3";
    private Tag tag1;
    private Tag tag2;
    private Item item1;
    private Item item2;
    private Item item3;

    @Test
    public void persist() throws Exception {
        Date yesterdayDate = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));
        Date weekAgoDate = Date.from(Instant.now().minus(7, ChronoUnit.DAYS));

        tag1 = new Tag(0, TEST_1, new ArrayList<>());
        tag2 = new Tag(0, TEST_2, new ArrayList<>());
        item1 = new Item(0, TEST_1, TEST_1, TEST_1, weekAgoDate, true, new ArrayList<>());
        item2 = new Item(0, TEST_2, TEST_2, TEST_2, yesterdayDate, true, new ArrayList<>());
        item3 = new Item(0, TEST_3, TEST_3, TEST_3, new Date(), true, new ArrayList<>());

        tag1.getItems().add(item1);
        tag2.getItems().add(item1);
        tag2.getItems().add(item2);
        item1.getTags().add(tag1);
        item1.getTags().add(tag2);
        item2.getTags().add(tag2);

        transaction.begin();
        try {
            em.persist(tag1);
            em.persist(tag2);
            em.persist(item1);
            em.persist(item2);
            em.persist(item3);
        } catch (ConstraintViolationException e) {
            showConstraintViolations(e);
        }
        transaction.commit();

        assertThat(item1.getId(), is(not(allOf(equalTo(item2.getId()), equalTo(item3.getId()), equalTo(0L)))));
        assertThat(tag1.getId(), is(not(equalTo(tag2.getId()))));
        queryTests();
    }

    private void queryTests() {
        List<Item> itemResultList = em.createNamedQuery(Item.GET_ALL, Item.class).getResultList();
        assertThat(itemResultList.size(), is(3));
        assertTrue(itemResultList.contains(item1));
        assertTrue(itemResultList.contains(item2));
        assertTrue(itemResultList.contains(item3));

        Item itemSingleResult = em.createNamedQuery(Item.GET_BY_OWNER, Item.class)
                .setParameter("owner", TEST_1)
                .getSingleResult();
        assertThat(itemSingleResult, is(item1));

        itemSingleResult = em.createNamedQuery(Item.GET_OLDEST, Item.class)
                .setFirstResult(0)
                .setMaxResults(1)
                .getSingleResult();
        assertThat(itemSingleResult, is(item3));

        Tag tagSingleResult = em.createNamedQuery(Tag.GET_BY_NAME, Tag.class)
                .setParameter("name", TEST_1)
                .getSingleResult();
        assertThat(tagSingleResult, is(tag1));

        List<Tag> tagResultList = em.createNamedQuery(Tag.GET_ALL, Tag.class).getResultList();
        assertThat(tagResultList.size(), is(2));
        assertTrue(tagResultList.contains(tag1));
        assertTrue(tagResultList.contains(tag2));
        int sumOfTaggedItems = tagResultList.stream().mapToInt(value -> {
            System.out.println(value.getItems().size());
            return value.getItems().size();
        }).sum();
        assertThat(sumOfTaggedItems, is(3));
    }
}