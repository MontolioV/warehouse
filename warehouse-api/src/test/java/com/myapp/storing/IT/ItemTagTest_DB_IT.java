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
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.RollbackException;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolationException;
import java.io.File;
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
    public static WebArchive createDeployment() {
        File libFile = Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("org.apache.commons:commons-lang3").withTransitivity().asSingleFile();
        WebArchive javaArchive = ShrinkWrap.create(WebArchive.class)
                .addClasses(Item.class, TextItem.class, FileItem.class, Tag.class)
                .addAsLibraries(libFile)
                .addAsWebInfResource("test-persistence.xml", "classes/META-INF/persistence.xml ")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new StringAsset("<web-app></web-app>"), "web.xml");
        return javaArchive;
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

        tags.add(new Tag(0, null, null));
        tags.add(new Tag(0, "", null));
        tags.add(new Tag(0, repeat("1", 31), null));
        tags.add(new Tag(0, TEST_1, null));

        persisted += persistAll(items);
        persisted += persistAll(tags);

        assertThat(persisted, is(0));
    }

    private int persistAll(List<?> objects) throws Exception {
        int persisted = 0;
        for (Object object : objects) {
            try {
                em.persist(object);
                tearDown();
                persisted++;
            } catch (ConstraintViolationException e) {
                transaction.rollback();
            } catch (RollbackException e) {
            } finally {
                setUp();
            }
        }
        return persisted;
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

        tagSingleResult = em.createNamedQuery(Tag.GET_BY_NAME, Tag.class)
                .setParameter(NAME_PARAM, TEST_2)
                .getSingleResult();
        assertThat(tagSingleResult.getName(), is(TEST_2));
        assertThat(tagSingleResult.getItems().size(), is(2));

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
    public void removeTags() {
        List<Item> itemResultList = em.createNamedQuery(Item.GET_ALL, Item.class).getResultList();
        assertThat(itemResultList.size(), is(7));

        List<Tag> tagResultList = em.createNamedQuery(Tag.GET_ALL, Tag.class).getResultList();
        assertThat(tagResultList.size(), is(2));
        tagResultList.forEach(em::remove);
    }

    @Test
    @InSequence(6)
    public void noTagsInDB() {
        List<Item> itemResultList = em.createNamedQuery(Item.GET_ALL, Item.class).getResultList();
        assertThat(itemResultList.size(), is(7));
        List<Tag> tagResultList = em.createNamedQuery(Tag.GET_ALL, Tag.class).getResultList();
        assertThat(tagResultList.size(), is(0));
    }
}