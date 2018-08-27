package com.myapp.storing.UT;

import com.myapp.storing.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.primefaces.model.DualListModel;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.myapp.utils.TestSecurityConstants.LOGIN_VALID;
import static com.myapp.utils.TestSecurityConstants.PASS_HASH_VALID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
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
    private FileStore fsMock;
    @Mock
    private TagStore tsMock;
    @Mock
    private FacesContext fcMock;
    @Mock
    private ExternalContext ecMock;
    @Mock
    private Part partMock;
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
        when(partMock.getContentType()).thenReturn(cType);
        when(partMock.getSubmittedFileName()).thenReturn(fName);
        when(partMock.getSize()).thenReturn(fSize);
        when(tsMock.fetchTagNames()).thenReturn(tagNamesSource);

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

        verify(isMock).saveItems(textItem);
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
        when(fsMock.persistFile(partMock)).thenReturn(PASS_HASH_VALID);

        controller.setTmpFile(partMock);
        FileItem fileItem = new FileItem();
        controller.setFileItem(fileItem);
        controller.setNewTagNames(tags);
        selectAllExistingTags();

        controller.createFileItem();

        assertThat(fileItem.getContentType(), is(cType));
        assertThat(fileItem.getNativeName(), is(fName));
        assertThat(fileItem.getSize(), is(fSize));
        assertThat(fileItem.getHash(), is(PASS_HASH_VALID));

        verify(isMock).saveItems(fileItem);
        verify(tsMock).saveTag(tag1, fileItem);
        verify(tsMock).saveTag(tag2, fileItem);
        verify(tsMock).saveTag(tag3, fileItem);
        verify(tsMock).saveTag(tagExisting, fileItem);
        assertNotNull(fileItem.getCreationDate());
        assertThat(fileItem.getOwner(), is(LOGIN_VALID));

        fileItem = new FileItem();
        controller.setPrincipal(null);
        controller.createFileItem();
        assertNull(fileItem.getOwner());
    }

    @Test
    public void createFileItemTooLarge() throws IOException {
        when(partMock.getSize()).thenReturn((long) FileItem.MAX_SIZE_BYTE + 1);
        controller.setTmpFile(partMock);
        controller.createFileItem();

        verify(fcMock).addMessage(eq("fileInput"), any(FacesMessage.class));
        verify(isMock, never()).saveItems(any());
        verify(tsMock, never()).saveTag(any(), any());
    }

    @Test
    public void createFileItemNoFile() throws IOException {
        controller.setTmpFile(null);
        controller.createFileItem();

        verify(isMock, never()).saveItems(any());
        verify(tsMock, never()).saveTag(any(), any());
    }

    @Test
    public void createFileItemIOException() throws IOException {
        when(fsMock.persistFile(partMock)).thenThrow(new IOException());

        controller.setTmpFile(partMock);
        FileItem fileItem = new FileItem();
        controller.setFileItem(fileItem);
        controller.setNewTagNames(tags);
        controller.createFileItem();

        verify(fcMock).addMessage(eq("fileInput"), any(FacesMessage.class));
        verify(isMock, never()).saveItems(any());
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
        when(tsMock.fetchTagsLikeName("name")).thenReturn(tags);

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