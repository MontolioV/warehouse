package com.myapp.storing.UT;

import com.myapp.storing.Item;
import com.myapp.storing.Tag;
import com.myapp.storing.TagStoreDB;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.myapp.storing.Tag.NAME_PARAM;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 17.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class TagStoreDBTest {
    @InjectMocks
    private TagStoreDB tagStoreDB;
    @Mock
    private EntityManager emMock;
    @Mock
    private TypedQuery<Tag> nameQMock;
    @Mock
    private TypedQuery<Tag> likeNameQMock;
    @Mock
    private TypedQuery<Tag> popularQMock;
    private String tagStringRaw = " tagString ";
    private String tagStringFormatted = "tagString";
    private Item item1;
    private Item item2;
    private ArrayList<Tag> tags = new ArrayList<>();

    @Before
    public void setUp() {
        item1 = new Item();
        item2 = new Item();
        item1.setId(1);
        item2.setId(2);
        when(emMock.find(Item.class, item1.getId())).thenReturn(item1);
        when(emMock.find(Item.class, item2.getId())).thenReturn(item2);
        when(emMock.createNamedQuery(Tag.GET_BY_NAME, Tag.class)).thenReturn(nameQMock);
        when(nameQMock.setParameter(NAME_PARAM, tagStringFormatted)).thenReturn(nameQMock);
        when(emMock.createNamedQuery(Tag.GET_LIKE_NAME, Tag.class)).thenReturn(likeNameQMock);
    }

    @Test
    public void saveTagExisting() {
        ArrayList<Tag> tags = new ArrayList<>();
        Tag existingTag = new Tag();
        existingTag.setName("existing");
        tags.add(existingTag);
        when(nameQMock.getResultList()).thenReturn(tags);

        tagStoreDB.saveTag(tagStringRaw, item1, item2);

        verify(emMock, never()).persist(any(Tag.class));
        existingTag.getItems().forEach(item -> assertThat(item, anyOf(sameInstance(item1), sameInstance(item2))));
        assertThat(existingTag.getLazyItemCounter(), is(2));
    }

    @Test
    public void saveTagNew() {
        ArrayList<Tag> tags = new ArrayList<>();
        when(nameQMock.getResultList()).thenReturn(tags);

        tagStoreDB.saveTag(tagStringRaw, item1, item2);

        ArgumentCaptor<Tag> tagArgumentCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(emMock).persist(tagArgumentCaptor.capture());
        Tag capturedTag = tagArgumentCaptor.getValue();
        assertThat(capturedTag.getName(), is(tagStringFormatted));
        assertTrue(item1.getTags().contains(capturedTag));
        assertTrue(item2.getTags().contains(capturedTag));
        capturedTag.getItems().forEach(item -> assertThat(item, anyOf(sameInstance(item1), sameInstance(item2))));
        assertThat(capturedTag.getLazyItemCounter(), is(2));
    }

    @Test
    public void executeCustomSelectQuery() {
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag());
        tags.add(new Tag());
        CriteriaQuery<Tag> cqMock = mock(CriteriaQuery.class);
        when(emMock.createQuery(cqMock)).thenReturn(nameQMock);
        when(emMock.createQuery(cqMock)).thenReturn(nameQMock);
        when(nameQMock.getResultList()).thenReturn(tags);

        List<Tag> result = tagStoreDB.executeCustomSelectQuery(cqMock);
        MatcherAssert.assertThat(result, sameInstance(tags));
    }

    @Test
    public void fetchMostPopularTags() {
        TypedQuery<Tag> maxQueryMock = mock(TypedQuery.class);
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(new Tag(0, "bce"));
        tags.add(new Tag(0, "abc"));
        when(emMock.createNamedQuery(Tag.GET_MOST_POPULAR, Tag.class)).thenReturn(popularQMock);
        when(popularQMock.setMaxResults(7)).thenReturn(maxQueryMock);
        when(maxQueryMock.getResultList()).thenReturn(tags);

        List<Tag> result = tagStoreDB.fetchMostPopularTags(7);
        assertThat(result, sameInstance(tags));
        assertThat(result.get(0).getName(), is("abc"));
    }

    @Test
    public void fetchTagsLikeName() {
        TypedQuery<Tag> paramQueryMock = mock(TypedQuery.class);
        when(likeNameQMock.setParameter(Tag.NAME_PARAM, "%" + tagStringFormatted + "%")).thenReturn(paramQueryMock);
        when(paramQueryMock.getResultList()).thenReturn(tags);

        List<Tag> tagList = tagStoreDB.fetchTagsNameContains(tagStringFormatted);
        assertThat(tagList, sameInstance(tags));
    }

    @Test
    public void fetchTagsStartsWithName() {
        TypedQuery<Tag> paramQueryMock = mock(TypedQuery.class);
        when(likeNameQMock.setParameter(Tag.NAME_PARAM, "%" + tagStringFormatted)).thenReturn(paramQueryMock);
        when(paramQueryMock.getResultList()).thenReturn(tags);

        List<Tag> tagList = tagStoreDB.fetchTagsNameStartsWith(tagStringFormatted);
        assertThat(tagList, sameInstance(tags));
    }

    @Test
    public void fetchTagsNameEndsWith() {
        TypedQuery<Tag> paramQueryMock = mock(TypedQuery.class);
        when(likeNameQMock.setParameter(Tag.NAME_PARAM, tagStringFormatted + "%")).thenReturn(paramQueryMock);
        when(paramQueryMock.getResultList()).thenReturn(tags);

        List<Tag> tagList = tagStoreDB.fetchTagsNameEndsWith(tagStringFormatted);
        assertThat(tagList, sameInstance(tags));
    }

    @Test
    public void fetchTagNames() {
        Tag tag1 = mock(Tag.class);
        Tag tag2 = mock(Tag.class);
        List<Tag> tagList = newArrayList(tag1, tag2);
        when(tag1.getName()).thenReturn("1");
        when(tag2.getName()).thenReturn("2");
        when(emMock.createNamedQuery(Tag.GET_ALL, Tag.class)).thenReturn(nameQMock);
        when(nameQMock.getResultList()).thenReturn(tagList);

        List<String> names = tagStoreDB.fetchTagNames();
        assertThat(names, containsInAnyOrder("1", "2"));
    }
}