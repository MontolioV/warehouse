package com.myapp.storing.UT;

import com.myapp.storing.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.primefaces.model.DualListModel;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.myapp.utils.TestSecurityConstants.LOGIN_VALID;
import static com.myapp.utils.TestSecurityConstants.PASS_HASH_VALID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * <p>Created by MontolioV on 17.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateItemControllerTest {
    @InjectMocks
    private CreateItemController controller;
    @Mock
    private ItemStore isMock;
    @Mock
    private TagStore tsMock;
    @Mock
    private UploadFilesCollector ufCollectorMock;
    @Mock
    private FacesContext fcMock;
    @Mock
    private ExternalContext ecMock;
    private Principal principalMock;
    private ArrayList<String> tags;
    private String tag1 = "tag1";
    private String tag2 = "tag2";
    private String tag3 = "tag3";
    private String tagExisting = "tagExisting";
    private String fName = "fName";
    private String cType = "cType";
    private long fSize = 10L;

    @Before
    public void setUp() throws Exception {
        ArrayList<String> tagNamesSource = newArrayList(tagExisting);
        principalMock = mock(Principal.class);
        tags = newArrayList(tag1, tag2, tag3);
        when(fcMock.getExternalContext()).thenReturn(ecMock);
        when(ecMock.getUserPrincipal()).thenReturn(principalMock);
        when(principalMock.getName()).thenReturn(LOGIN_VALID);
        when(tsMock.fetchTagNames()).thenReturn(tagNamesSource);

        FileItem temporalFileItem = new FileItem();
        temporalFileItem.setContentType(cType);
        temporalFileItem.setHash(PASS_HASH_VALID);
        temporalFileItem.setNativeName(fName);
        temporalFileItem.setSize(fSize);
        LinkedList<FileItem> linkedList = new LinkedList<>();
        linkedList.add(temporalFileItem);
        when(ufCollectorMock.getTemporalFileItems()).thenReturn(linkedList);

        controller.init();
    }

    private void selectAllExistingTags() {
        DualListModel<String> dlm = controller.getExistingTagNamesDualListModel();
        dlm.setTarget(dlm.getSource());
        dlm.setSource(new ArrayList<>());
    }

    @Test
    public void createTextItem() throws IOException {
        TextItem textItem = new TextItem();
        controller.setTextItem(textItem);
        controller.setNewTagNames(tags);
        selectAllExistingTags();

        controller.createTextItem();

        verify(isMock).persistItems(textItem);
        verify(tsMock).saveTag(tag1, textItem);
        verify(tsMock).saveTag(tag2, textItem);
        verify(tsMock).saveTag(tag3, textItem);
        verify(tsMock).saveTag(tagExisting, textItem);
        assertNotNull(textItem.getCreationDate());
        assertThat(textItem.getOwner(), is(LOGIN_VALID));

        textItem = new TextItem();
        controller.setPrincipal(null);
        controller.createTextItem();
        assertNull(textItem.getOwner());
    }

    @Test
    public void createFileItem() throws IOException {
        FileItem fileItem = new FileItem();
        controller.setFileItem(fileItem);
        controller.setNewTagNames(tags);
        selectAllExistingTags();

        controller.createFileItems();

        ArgumentCaptor<FileItem> captor = ArgumentCaptor.forClass(FileItem.class);
        verify(isMock).saveItems(captor.capture());
        verify(isMock, never()).saveItems(fileItem);

        FileItem captorValue = captor.getValue();
        verify(tsMock).saveTag(tag1, captorValue);
        verify(tsMock).saveTag(tag2, captorValue);
        verify(tsMock).saveTag(tag3, captorValue);
        verify(tsMock).saveTag(tagExisting, captorValue);
        verify(fcMock).addMessage(eq(null), any(FacesMessage.class));

        assertThat(captorValue, not(sameInstance(fileItem)));
        assertThat(captorValue.getContentType(), is(cType));
        assertThat(captorValue.getNativeName(), is(fName));
        assertThat(captorValue.getSize(), is(fSize));
        assertThat(captorValue.getHash(), is(PASS_HASH_VALID));
        assertNotNull(captorValue.getCreationDate());
        assertThat(captorValue.getOwner(), is(LOGIN_VALID));

        fileItem = new FileItem();
        controller.setPrincipal(null);
        controller.createFileItems();
        assertNull(fileItem.getOwner());
    }

    @Test
    public void createFileItems() {
        LinkedList<FileItem> linkedList = new LinkedList<>();
        for (int i = 0; i < 2; i++) {
            FileItem temporalFileItem = new FileItem();
            temporalFileItem.setContentType(cType);
            temporalFileItem.setHash(PASS_HASH_VALID);
            temporalFileItem.setNativeName(fName);
            temporalFileItem.setSize(fSize);
            linkedList.add(temporalFileItem);
        }
        when(ufCollectorMock.getTemporalFileItems()).thenReturn(linkedList);

        FileItem fileItem = new FileItem();
        controller.setFileItem(fileItem);
        controller.setNewTagNames(tags);
        selectAllExistingTags();

        controller.createFileItems();

        ArgumentCaptor<FileItem> captor = ArgumentCaptor.forClass(FileItem.class);
        verify(isMock, times(2)).saveItems(captor.capture());
        verify(isMock, never()).saveItems(fileItem);

        List<FileItem> allValues = captor.getAllValues();
        for (FileItem value : allValues) {
            verify(tsMock).saveTag(tag1, value);
            verify(tsMock).saveTag(tag2, value);
            verify(tsMock).saveTag(tag3, value);
            verify(tsMock).saveTag(tagExisting, value);

            assertThat(fileItem, not(sameInstance(value)));
            assertThat(value.getName(), is(fileItem.getName()));
            assertThat(value.getDescription(), is(fileItem.getDescription()));
            assertTrue(value.isShared());
            assertTrue(fileItem.isShared());
        }
        assertThat(allValues.get(0), not(sameInstance(allValues.get(1))));
    }

    @Test
    public void createFileItemNoFile() throws IOException {
        when(ufCollectorMock.getTemporalFileItems()).thenReturn(newLinkedList());

        controller.createFileItems();

        verify(isMock, never()).persistItems(any());
        verify(tsMock, never()).saveTag(any(), any());
    }

    @Test
    public void tagCreation() throws IOException {
        controller.setNewTagNames(null);
        controller.createTextItem();
        verify(tsMock, never()).saveTag(anyString(), any());

        tags = newArrayList("tag", "tag", "tag");
        TextItem textItem = new TextItem();
        controller.setTextItem(textItem);
        controller.setNewTagNames(tags);
        controller.createTextItem();

        verify(tsMock).saveTag("tag", textItem);
    }

    @Test
    public void autocompleteTags() {
        Tag tag1Mock = mock(Tag.class);
        Tag tag2Mock = mock(Tag.class);
        ArrayList<Tag> tags = newArrayList(tag1Mock, tag2Mock);
        when(tag1Mock.getName()).thenReturn(tag1);
        when(tag2Mock.getName()).thenReturn(tag2);
        when(tsMock.fetchTagsNameStartsWith("name")).thenReturn(tags);

        List<String> names = controller.autocompleteTags("name");
        assertThat(names.size(), is(2));
        assertThat(names.get(0), is(tag1));
        assertThat(names.get(1), is(tag2));
    }

    @Test
    public void init() {
        DualListModel<String> dlm = controller.getExistingTagNamesDualListModel();

        assertThat(controller.getPrincipal(), sameInstance(principalMock));
        assertThat(dlm.getSource(), containsInAnyOrder(tagExisting));
        assertTrue(dlm.getTarget().isEmpty());
    }
}