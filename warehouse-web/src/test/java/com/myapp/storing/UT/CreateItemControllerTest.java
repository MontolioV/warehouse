package com.myapp.storing.UT;

import com.myapp.storing.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;

import static com.myapp.utils.TestSecurityConstants.LOGIN_VALID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
    private FacesContext fcMock;
    @Mock
    private ExternalContext ecMock;
    @Mock
    private Principal principalMock;
    private String tagsString;
    private String tag1 = "tag1";
    private String tag2 = "tag2";
    private String tag3 = "tag3";

    @Before
    public void setUp() throws Exception {
        tagsString = tag1 + "\n" + tag2 + "\n" + tag3;
        when(fcMock.getExternalContext()).thenReturn(ecMock);
        when(ecMock.getUserPrincipal()).thenReturn(principalMock);
        when(principalMock.getName()).thenReturn(LOGIN_VALID);
    }

    @Test
    public void createTextItem() throws IOException {
        TextItem textItem = new TextItem();
        controller.setTextItem(textItem);
        controller.setTagsString(tagsString);

        controller.createTextItem();

        verify(isMock).saveItems(textItem);
        verify(tsMock).saveTag(tag1, textItem);
        verify(tsMock).saveTag(tag2, textItem);
        verify(tsMock).saveTag(tag3, textItem);
        assertNotNull(textItem.getCreationDate());
        assertThat(textItem.getOwner(), is(LOGIN_VALID));

        textItem = new TextItem();
        controller.setPrincipal(null);
        controller.createTextItem();
        assertNull(textItem.getOwner());
    }

    @Test
    public void createFileItem() throws IOException {
        String fName = "fName";
        String cType = "cType";
        long fSize = 10L;
        InputStream streamMock = mock(InputStream.class);
        Part partMock = mock(Part.class);
        when(partMock.getContentType()).thenReturn(cType);
        when(partMock.getSubmittedFileName()).thenReturn(fName);
        when(partMock.getSize()).thenReturn(fSize);
        when(partMock.getInputStream()).thenReturn(streamMock);

        controller.setTmpFile(partMock);
        FileItem fileItem = new FileItem();
        controller.setFileItem(fileItem);
        controller.setTagsString(tagsString);

        controller.createFileItem();

        assertThat(fileItem.getBinaryData(), notNullValue());
        assertThat(fileItem.getContentType(), is(cType));
        assertThat(fileItem.getNativeName(), is(fName));
        assertThat(fileItem.getSize(), is(fSize));
        assertThat(((long) fileItem.getBinaryData().length), is(fSize));
        verify(streamMock).read(fileItem.getBinaryData());

        verify(isMock).saveItems(fileItem);
        verify(tsMock).saveTag(tag1, fileItem);
        verify(tsMock).saveTag(tag2, fileItem);
        verify(tsMock).saveTag(tag3, fileItem);
        assertNotNull(fileItem.getCreationDate());
        assertThat(fileItem.getOwner(), is(LOGIN_VALID));

        fileItem = new FileItem();
        controller.setPrincipal(null);
        controller.createFileItem();
        assertNull(fileItem.getOwner());
    }

    @Test
    public void createFileItemTooLarge() throws IOException {
        Part partMock = mock(Part.class);

        when(partMock.getSize()).thenReturn((long) FileItem.MAX_SIZE_BYTE + 1);
        controller.setTmpFile(partMock);
        controller.createFileItem();

        verify(fcMock).addMessage(eq("fileInput"), any(FacesMessage.class));
        verify(isMock, never()).saveItems(any());
        verify(tsMock, never()).saveTag(any(), any());
    }

    @Test
    public void createFileItemNoFile() throws IOException {
        controller.createFileItem();

        verify(isMock, never()).saveItems(any());
        verify(tsMock, never()).saveTag(any(), any());
    }

}