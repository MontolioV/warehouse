package com.myapp.storing.IT;

import com.myapp.storing.FileItem;
import com.myapp.storing.Item;
import com.myapp.storing.Tag;
import com.myapp.storing.TextItem;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static com.myapp.TestUtils.showConstraintViolations;
import static com.myapp.storing.Item.CLASS_PARAM;
import static com.myapp.storing.Item.OWNER_PARAM;
import static com.myapp.storing.Tag.NAME_PARAM;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * <p>Created by MontolioV on 16.04.18.
 */

@RunWith(Arquillian.class)
public class ItemTagTest_DB_IT {
    private final String TEST_1 = "TEST_1";
    private final String TEST_2 = "TEST_2"; 
    private final String TEST_3 = "TEST_3";
    private final String TEST_4 = "TEST_4";
    private final String TEST_5 = "TEST_5";
    private final String NATIVE_NAME = "NATIVE_NAME";
    private final String CONTENT_TYPE = "CONTENT_TYPE";
    private final String HASH_1 = "HASH_1";
    private final String HASH_2 = "HASH_2";

    @PersistenceContext
    private EntityManager em;
    @Inject
    private UserTransaction transaction;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClasses(Item.class, TextItem.class, FileItem.class, Tag.class)
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Before
    public void setUp() throws Exception {
        transaction.begin();
        em.joinTransaction();
    }

    @After
    public void tearDown() throws Exception {
        transaction.commit();
        em.clear();
    }

    @Test
    @InSequence(1)
    public void persistAcceptable() throws Exception {
        Date minuteAgoDate = Date.from(Instant.now().minus(1, ChronoUnit.MINUTES));
        Date yesterdayDate = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));
        Date weekAgoDate = Date.from(Instant.now().minus(7, ChronoUnit.DAYS));

        Tag tag1 = new Tag(0, TEST_1, new HashSet<>());
        Tag tag2 = new Tag(0, TEST_2, new HashSet<>());
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

        ArrayList<Item> items = new ArrayList<>();
        items.add(item1_private);
        items.add(item2);
        items.add(item3);
        items.add(nullsAllowed);
        items.add(textItem);
        items.add(fileItem1);
        items.add(fileItem2);
        items.add(fileItem3);

        try {
            items.forEach(em::persist);
        } catch (ConstraintViolationException e) {
            showConstraintViolations(e);
        }
    }

    @Test
    @InSequence(2)
    public void persistUnAcceptable() {
        int violations = 0;
        ArrayList<Item> items = new ArrayList<>();
        ArrayList<Tag> tags = new ArrayList<>();

        items.add(new Item(0, null, null, repeat("1", 31), null, true, null));
        items.add(new Item(0, "", null, null, Date.from(Instant.now().minus(1, ChronoUnit.MINUTES)), true, null));
        items.add(new Item(0, repeat("1", 31), null, null, new Date(), true, null));
        items.add(new TextItem(0, TEST_1, null, null, new Date(), true, null,repeat("1", 100_001)));
        items.add(new FileItem(0, TEST_1, null, null, new Date(), true, null,
                null, null, FileItem.MAX_SIZE_BYTE + 1, null));
        items.add(new FileItem(0, TEST_1, null, null, new Date(), true, null,
                "", "", 0, ""));

        tags.add(new Tag(0, null, null));
        tags.add(new Tag(0, "", null));
        tags.add(new Tag(0, repeat("1", 31), null));
        tags.add(new Tag(0, TEST_1, null));

        for (Item item : items) {
            try {
                em.persist(item);
            } catch (ConstraintViolationException e) {
                violations += e.getConstraintViolations().size();
            }
        }
        for (Tag tag : tags) {
            try {
                em.persist(tag);
            } catch (ConstraintViolationException e) {
                violations += e.getConstraintViolations().size();
            }
        }

        assertThat(violations, is(17));
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

        Tag tagSingleResult = em.createNamedQuery(Tag.GET_BY_NAME, Tag.class)
                .setParameter(NAME_PARAM, TEST_1)
                .getSingleResult();
        assertThat(tagSingleResult.getName(), is(TEST_1));
        assertThat(tagSingleResult.getItems().size(), is(1));

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
    }

    @Test
    @InSequence(4)
    public void remove() {
        Tag tag = em.createNamedQuery(Tag.GET_BY_NAME, Tag.class).setParameter(Tag.NAME_PARAM, TEST_1).getSingleResult();
        assertThat(tag.getItems().size(), is(1));
        em.remove(tag.getItems().iterator().next());
        List<Item> itemResultList = em.createNamedQuery(Item.GET_ALL, Item.class).getResultList();
        assertThat(itemResultList.size(), is(7));
        List<Tag> tagResultList = em.createNamedQuery(Tag.GET_ALL, Tag.class).getResultList();
        assertThat(tagResultList.size(), is(2));

        tagResultList.forEach(em::remove);
        itemResultList = em.createNamedQuery(Item.GET_ALL, Item.class).getResultList();
        assertThat(itemResultList.size(), is(7));
        tagResultList = em.createNamedQuery(Tag.GET_ALL, Tag.class).getResultList();
        assertThat(tagResultList.size(), is(0));
    }
}