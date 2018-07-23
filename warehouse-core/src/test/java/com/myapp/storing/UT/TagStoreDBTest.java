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

import static com.myapp.storing.Tag.NAME_PARAM;
import static org.hamcrest.CoreMatchers.*;
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
    private TypedQuery<Tag> queryMock;
    private String tagString = "tagString";
    private Item item1;
    private Item item2;

    @Before
    public void setUp() {
        item1 = new Item();
        item2 = new Item();
        item1.setId(1);
        item2.setId(2);
        when(emMock.find(Item.class, item1.getId())).thenReturn(item1);
        when(emMock.find(Item.class, item2.getId())).thenReturn(item2);
        when(emMock.createNamedQuery(Tag.GET_BY_NAME, Tag.class)).thenReturn(queryMock);
        when(queryMock.setParameter(NAME_PARAM, tagString)).thenReturn(queryMock);
    }

    @Test
    public void saveTagExisting() {
        ArrayList<Tag> tags = new ArrayList<>();
        Tag existingTag = new Tag();
        existingTag.setName("existing");
        tags.add(existingTag);
        when(queryMock.getResultList()).thenReturn(tags);

        tagStoreDB.saveTag(tagString, item1, item2);

        verify(emMock, never()).persist(any(Tag.class));
        existingTag.getItems().forEach(item -> assertThat(item, anyOf(sameInstance(item1), sameInstance(item2))));
    }

    @Test
    public void saveTagNew() {
        ArrayList<Tag> tags = new ArrayList<>();
        when(queryMock.getResultList()).thenReturn(tags);

        tagStoreDB.saveTag(tagString, item1, item2);

        ArgumentCaptor<Tag> tagArgumentCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(emMock).persist(tagArgumentCaptor.capture());
        Tag capturedTag = tagArgumentCaptor.getValue();
        assertThat(capturedTag.getName(), is(tagString));
        assertTrue(item1.getTags().contains(capturedTag));
        assertTrue(item2.getTags().contains(capturedTag));
        capturedTag.getItems().forEach(item -> assertThat(item, anyOf(sameInstance(item1), sameInstance(item2))));
    }

    @Test
    public void executeCustomSelectQuery() {
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag());
        tags.add(new Tag());
        CriteriaQuery<Tag> cqMock = mock(CriteriaQuery.class);
        when(emMock.createQuery(cqMock)).thenReturn(queryMock);
        when(emMock.createQuery(cqMock)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(tags);

        List<Tag> result = tagStoreDB.executeCustomSelectQuery(cqMock);
        MatcherAssert.assertThat(result, sameInstance(tags));
    }

}