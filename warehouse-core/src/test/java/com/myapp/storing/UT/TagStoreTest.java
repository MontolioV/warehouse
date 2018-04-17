package com.myapp.storing.UT;

import com.myapp.storing.Item;
import com.myapp.storing.Tag;
import com.myapp.storing.TagStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 17.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class TagStoreTest {
    @InjectMocks
    private TagStore tagStore;
    @Mock
    private EntityManager emMock;
    @Mock
    private TypedQuery<Tag> queryMock;
    private String tagString = "tagString";

    @Test
    public void saveTagExisting() {
        ArrayList<Tag> tags = new ArrayList<>();
        Tag existingTag = new Tag();
        tags.add(existingTag);
        when(emMock.createNamedQuery(Tag.GET_BY_NAME, Tag.class)).thenReturn(queryMock);
        when(queryMock.setParameter("name", tagString)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(tags);

        Item item1 = new Item();
        Item item2 = new Item();

        tagStore.saveTag(tagString, item1, item2);

        verify(emMock, never()).persist(any(Tag.class));
        assertTrue(existingTag.getItems().contains(item1));
        assertTrue(existingTag.getItems().contains(item2));
    }

    @Test
    public void saveTagNew() {
        ArrayList<Tag> tags = new ArrayList<>();
        when(emMock.createNamedQuery(Tag.GET_BY_NAME, Tag.class)).thenReturn(queryMock);
        when(queryMock.setParameter("name", tagString)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(tags);

        Item item1 = new Item();
        Item item2 = new Item();

        tagStore.saveTag(tagString, item1, item2);

        ArgumentCaptor<Tag> tagArgumentCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(emMock).persist(tagArgumentCaptor.capture());
        assertTrue(tagArgumentCaptor.getValue().getItems().contains(item1));
        assertTrue(tagArgumentCaptor.getValue().getItems().contains(item2));
    }
}