package com.myapp.storing.IT;

import com.myapp.IT.AbstractITArquillianWithEM;
import com.myapp.storing.FileItem;
import com.myapp.storing.Item;
import com.myapp.storing.Tag;
import com.myapp.storing.TextItem;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import javax.persistence.OptimisticLockException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.function.Consumer;

import static com.myapp.storing.Item.*;
import static com.myapp.storing.Tag.NAME_PARAM;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * <p>Created by MontolioV on 16.04.18.
 */

public class ItemTag_DB_IT extends AbstractITArquillianWithEM {
    private final String TEST_1 = "TEST_1";
    private final String TEST_2 = "TEST_2";
    private final String TEST_3 = "TEST_3";
    private final String TEST_4 = "TEST_4";
    private final String TEST_5 = "TEST_5";
    private final String NATIVE_NAME = "NATIVE_NAME";
    private final String CONTENT_TYPE = "CONTENT_TYPE";
    private final String HASH_1 = "HASH_1";
    private final String HASH_2 = "HASH_2";

    @Deployment
    public static WebArchive createDeployment() {
        return AbstractITArquillianWithEM.createDeployment()
                .addClasses(Item.class, TextItem.class, FileItem.class, Tag.class);
    }

    @Test
    @InSequence(1)
    public void persistAcceptable() throws Exception {
        Date minuteAgoDate = Date.from(Instant.now().minus(1, ChronoUnit.MINUTES));
        Date yesterdayDate = Date.from(Instant.now().minus(1, DAYS));
        Date weekAgoDate = Date.from(Instant.now().minus(7, DAYS));

        Tag tag1 = new Tag(0, TEST_1);
        Tag tag2 = new Tag(0, TEST_2);
        Item item1_private = new Item(0, TEST_1, TEST_1, TEST_1, new Date(), false, new ArrayList<>());
        Item item2 = new Item(0, TEST_2, TEST_2, TEST_3, weekAgoDate, true, new ArrayList<>());
        Item item3 = new Item(0, TEST_3, TEST_3, TEST_3, yesterdayDate, true, new ArrayList<>());
        Item nullsAllowed = new Item(0, TEST_1, null, null, weekAgoDate, false, null);
        Item textItem = new TextItem(0, TEST_4, TEST_4, TEST_4, minuteAgoDate, true, new ArrayList<>(), TEST_4);
        FileItem fileItem1 = new FileItem(0, TEST_5, TEST_5, TEST_5, weekAgoDate, true, new ArrayList<>(), NATIVE_NAME, CONTENT_TYPE, 1L, HASH_1);
        FileItem fileItem2 = new FileItem(0, TEST_5, TEST_5, TEST_5, weekAgoDate, true, new ArrayList<>(), NATIVE_NAME, CONTENT_TYPE, 1L, HASH_2);
        FileItem fileItem3 = new FileItem(0, TEST_5, TEST_5, TEST_5, weekAgoDate, true, new ArrayList<>(), NATIVE_NAME, CONTENT_TYPE, 1L, HASH_1);

        tag1.getItems().add(item1_private);
        tag2.getItems().add(item1_private);
        tag2.getItems().add(item2);
        item1_private.getTags().add(tag1);
        item1_private.getTags().add(tag2);
        item2.getTags().add(tag2);

        tag1.updateLazyItemCounter();
        tag2.updateLazyItemCounter();

        ArrayList<Item> items = new ArrayList<>();
        items.add(item1_private);
        items.add(item2);
        items.add(item3);
        items.add(nullsAllowed);
        items.add(textItem);
        items.add(fileItem1);
        items.add(fileItem2);
        items.add(fileItem3);

        persistAllowed(items);
    }

    @Test
    @InSequence(2)
    public void persistUnAcceptable() throws Exception {
        int persisted = 0;
        ArrayList<Item> items = new ArrayList<>();
        ArrayList<Tag> tags = new ArrayList<>();

        items.add(new Item(0, null, null, null, new Date(), true, null));
        items.add(new Item(0, "", null, null, new Date(), true, null));
        items.add(new Item(0, repeat("1", 31), null, null, new Date(), true, null));
        items.add(new Item(0, TEST_1, null, repeat("1", 31), new Date(), true, null));
        items.add(new Item(0, TEST_1, null, null, null, true, null));
        items.add(new Item(0, TEST_1, null, null, Date.from(Instant.now().plus(1, ChronoUnit.MINUTES)), true, null));
        items.add(new TextItem(0, TEST_1, null, null, new Date(), true, null,repeat("1", 100_001)));
        items.add(new FileItem(0, TEST_1, null, null, new Date(), true, null,
                null, CONTENT_TYPE, FileItem.MAX_SIZE_BYTE, HASH_1));
        items.add(new FileItem(0, TEST_1, null, null, new Date(), true, null,
                "", CONTENT_TYPE, FileItem.MAX_SIZE_BYTE, HASH_1));
        items.add(new FileItem(0, TEST_1, null, null, new Date(), true, null,
                NATIVE_NAME, null, FileItem.MAX_SIZE_BYTE, HASH_1));
        items.add(new FileItem( 0, TEST_1, null, null, new Date(), true, null,
                NATIVE_NAME, "", FileItem.MAX_SIZE_BYTE, HASH_1));
        items.add(new FileItem(0, TEST_1, null, null, new Date(), true, null,
                NATIVE_NAME, CONTENT_TYPE, FileItem.MAX_SIZE_BYTE + 1, HASH_1));
        items.add(new FileItem(0, TEST_1, null, null, new Date(), true, null,
                NATIVE_NAME, CONTENT_TYPE, FileItem.MAX_SIZE_BYTE, null));
        items.add(new FileItem(0, TEST_1, null, null, new Date(), true, null,
                NATIVE_NAME, CONTENT_TYPE, FileItem.MAX_SIZE_BYTE, ""));

        tags.add(new Tag(0, null));
        tags.add(new Tag(0, ""));
        tags.add(new Tag(0, repeat("1", 31)));
        tags.add(new Tag(0, TEST_1));

        persisted += persistNotAllowed(items);
        persisted += persistNotAllowed(tags);

        assertThat(persisted, is(0));
    }

    @Test
    @InSequence(3)
    public void queryTests() {
        List<Item> itemResultList = em.createNamedQuery(Item.GET_ALL, Item.class).getResultList();
        assertThat(itemResultList.size(), is(8));

        itemResultList = em.createNamedQuery(Item.GET_ALL_BY_OWNER, Item.class)
                .setParameter(OWNER_PARAM, TEST_3)
                .getResultList();
        assertThat(itemResultList.size(), is(2));
        itemResultList.forEach(item -> assertThat(item.getOwner(), is(TEST_3)));

        itemResultList = em.createNamedQuery(Item.GET_ALL_OF_CLASS, Item.class)
                .setParameter(CLASS_PARAM, Item.class)
                .getResultList();
        assertThat(itemResultList.size(), is(4));
        itemResultList.forEach(item -> assertThat(item.getdType(), is(Item.class.getSimpleName())));

        Item aClass = em.createNamedQuery(Item.GET_ALL_OF_CLASS, Item.class)
                .setParameter(CLASS_PARAM, TextItem.class)
                .getSingleResult();
        assertThat(((TextItem) aClass).getText(), is(TEST_4));
        assertThat(aClass.getdType(), is(TextItem.class.getSimpleName()));

        itemResultList = em.createNamedQuery(Item.GET_BY_OWNER, Item.class)
                .setParameter(OWNER_PARAM, TEST_3)
                .getResultList();
        assertThat(itemResultList.size(), is(2));
        itemResultList.forEach(item -> assertThat(item.getOwner(), is(TEST_3)));

        Item itemSingleResult = em.createNamedQuery(Item.GET_BY_OWNER_OF_CLASS, Item.class)
                .setParameter(OWNER_PARAM, TEST_1)
                .setParameter(CLASS_PARAM, Item.class)
                .getSingleResult();
        assertThat(itemSingleResult.getOwner(), is(TEST_1));
        assertThat(itemSingleResult.getdType(), is(Item.class.getSimpleName()));

        itemSingleResult = em.createNamedQuery(Item.GET_LAST_SHARED, Item.class)
                .setFirstResult(0)
                .setMaxResults(1)
                .getSingleResult();
        assertThat(itemSingleResult.getName(), is(TEST_4));
        assertThat(itemSingleResult.getOwner(), is(TEST_4));
        assertThat(itemSingleResult.getdType(), is(TextItem.class.getSimpleName()));

        itemSingleResult = em.createNamedQuery(Item.GET_LAST_OF_CLASS, Item.class)
                .setParameter(CLASS_PARAM, Item.class)
                .setFirstResult(0)
                .setMaxResults(1)
                .getSingleResult();
        assertThat(itemSingleResult.getName(), is(TEST_1));
        assertThat(itemSingleResult.getOwner(), is(TEST_1));
        assertThat(itemSingleResult.getdType(), is(Item.class.getSimpleName()));

        Date date = Date.from(Instant.now().minus(5, DAYS));
        itemSingleResult = em.createNamedQuery(Item.GET_EXPIRED, Item.class)
                .setParameter(MINIMAL_CREATION_DATE_PARAM, date)
                .getSingleResult();
        assertThat(itemSingleResult.getName(), is(TEST_1));
        assertThat(itemSingleResult.getOwner(), nullValue());
        assertThat(itemSingleResult.getDescription(), nullValue());
        assertThat(itemSingleResult.getCreationDate(), lessThan(date));
        assertFalse(itemSingleResult.isShared());

        Tag tagSingleResult = em.createNamedQuery(Tag.GET_BY_NAME, Tag.class)
                .setParameter(NAME_PARAM, TEST_1)
                .getSingleResult();
        assertThat(tagSingleResult.getName(), is(TEST_1));
        assertThat(tagSingleResult.getItems().size(), is(1));

        tagSingleResult = em.createNamedQuery(Tag.GET_BY_NAME, Tag.class)
                .setParameter(NAME_PARAM, TEST_2)
                .getSingleResult();
        assertThat(tagSingleResult.getName(), is(TEST_2));
        assertThat(tagSingleResult.getItems().size(), is(2));
        assertThat(tagSingleResult.getLazyItemCounter(), is(2));

        tagSingleResult = em.createNamedQuery(Tag.GET_MOST_POPULAR, Tag.class)
                .setMaxResults(1)
                .getSingleResult();
        assertThat(tagSingleResult.getName(), is(TEST_2));
        assertThat(tagSingleResult.getItems().size(), is(2));
        assertThat(tagSingleResult.getLazyItemCounter(), is(2));

        List<Tag> tagResultList = em.createNamedQuery(Tag.GET_ALL, Tag.class).getResultList();
        assertThat(tagResultList.size(), is(2));
        int sumOfTaggedItems = tagResultList.stream().mapToInt(value -> value.getItems().size()).sum();
        assertThat(sumOfTaggedItems, is(3));

        tagResultList = em.createNamedQuery(Tag.GET_LIKE_NAME, Tag.class).setParameter(NAME_PARAM, "TEST").getResultList();
        assertThat(tagResultList.size(), is(2));
        tagResultList = em.createNamedQuery(Tag.GET_LIKE_NAME, Tag.class).setParameter(NAME_PARAM, "TEST_1").getResultList();
        assertThat(tagResultList.size(), is(1));

        List<String> hashesList = em.createNamedQuery(FileItem.GET_ALL_HASHES, String.class).getResultList();
        assertThat(hashesList.size(), is(2));
        assertTrue(hashesList.contains(HASH_1));
        assertTrue(hashesList.contains(HASH_2));

        long totalSize = em.createNamedQuery(FileItem.GET_TOTAL_SIZE_BY_OWNER, Long.class)
                .setParameter(Item.OWNER_PARAM, "TEST_5")
                .getSingleResult();
        assertThat(totalSize, is(3L));
    }

    @Test
    @InSequence(3)
    public void removeItemFromTag() {
        List<Item> itemResultList = em.createNamedQuery(Item.GET_ALL, Item.class).getResultList();
        assertThat(itemResultList.size(), is(8));
        Tag tag = em.createNamedQuery(Tag.GET_BY_NAME, Tag.class).setParameter(Tag.NAME_PARAM, TEST_2).getSingleResult();
        assertThat(tag.getItems().size(), is(2));
        Item item = tag.getItems().iterator().next();
        item.getTags().remove(tag);
        tag.getItems().remove(item);
    }

    @Test
    @InSequence(4)
    public void removeItemWithTags() {
        Tag tag = em.createNamedQuery(Tag.GET_BY_NAME, Tag.class).setParameter(Tag.NAME_PARAM, TEST_2).getSingleResult();
        assertThat(tag.getItems().size(), is(1));
        List<Item> itemResultList = em.createNamedQuery(Item.GET_ALL, Item.class).getResultList();
        assertThat(itemResultList.size(), is(8));

        tag = em.createNamedQuery(Tag.GET_BY_NAME, Tag.class).setParameter(Tag.NAME_PARAM, TEST_1).getSingleResult();
        assertThat(tag.getItems().size(), is(1));
        em.remove(tag.getItems().iterator().next());
    }

    @Test
    @InSequence(5)
    public void concurrency() throws Throwable {
        List<Item> itemResultList = em.createNamedQuery(Item.GET_ALL, Item.class).getResultList();
        assertThat(itemResultList.size(), is(7));

        List<Tag> tagResultList = em.createNamedQuery(Tag.GET_ALL, Tag.class).getResultList();
        assertThat(tagResultList.size(), is(2));
        Tag tag = em.createNamedQuery(Tag.GET_BY_NAME, Tag.class).setParameter(Tag.NAME_PARAM, TEST_1).getSingleResult();
        assertThat(tag.getLazyItemCounter(), is(0));

        Phaser phaser = new Phaser(2);
        Consumer<String> concurentUpdate = itemName -> {
            Tag otherTag = em.createNamedQuery(Tag.GET_BY_NAME, Tag.class).setParameter(Tag.NAME_PARAM, TEST_1).getSingleResult();
            phaser.arriveAndAwaitAdvance();
            Item otherItem = new Item();
            otherItem.setName(itemName);
            otherItem.setCreationDate(new Date());
            otherItem.getTags().add(otherTag);
            otherTag.getItems().add(otherItem);
            otherTag.updateLazyItemCounter();

            em.persist(otherItem);
            em.merge(otherTag);
        };
        Thread otherThread = new Thread(() -> {
            try {
                setUp();
                concurentUpdate.accept("Item_other");
                tearDown();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        otherThread.start();
        concurentUpdate.accept("Item");
        otherThread.join();

        try {
            em.flush();
        } catch (OptimisticLockException e) {
            handleTransactionRollback();
        }
    }

    @Test
    @InSequence(6)
    public void removeTags() {
        Tag tag = em.createNamedQuery(Tag.GET_BY_NAME, Tag.class).setParameter(Tag.NAME_PARAM, TEST_1).getSingleResult();
        assertThat(tag.getItems().size(), is(1));
        assertThat(tag.getItems().iterator().next().getName(), is("Item_other"));
        assertThat(tag.getLazyItemCounter(), is(1));


        List<Tag> tagResultList = em.createNamedQuery(Tag.GET_ALL, Tag.class).getResultList();
        tagResultList.forEach(em::remove);
    }

    @Test
    @InSequence(7)
    public void noTagsInDB() {
        List<Item> itemResultList = em.createNamedQuery(Item.GET_ALL, Item.class).getResultList();
        assertThat(itemResultList.size(), is(8));
        List<Tag> tagResultList = em.createNamedQuery(Tag.GET_ALL, Tag.class).getResultList();
        assertThat(tagResultList.size(), is(0));
    }
}