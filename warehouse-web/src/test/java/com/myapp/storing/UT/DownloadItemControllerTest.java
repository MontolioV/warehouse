package com.myapp.storing.UT;

import com.myapp.storing.DownloadItemController;
import com.myapp.storing.FileItem;
import com.myapp.storing.FileStore;
import com.myapp.storing.ItemStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 07.05.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class DownloadItemControllerTest {
    @InjectMocks
    private DownloadItemController controller;
    @Mock
    private FacesContext fcMock;
    @Mock
    private ExternalContext ecMock;
    @Mock
    private ItemStore isMock;
    @Mock
    private FileStore fsMock;
    @Mock
    private Principal principalMock;
    @Mock
    private FileItem itemMock;
    @Mock
    private OutputStream streamMock;

    @Test
    public void downloadItem() throws IOException {
        when(principalMock.getName()).thenReturn("");
        when(fcMock.getExternalContext()).thenReturn(ecMock);
        when(ecMock.getUserPrincipal()).thenReturn(principalMock);
        when(isMock.getItemById(anyLong(), anyString())).thenReturn(itemMock);
        when(itemMock.getSize()).thenReturn(10L);
        when(itemMock.getContentType()).thenReturn("getContentType");
        when(itemMock.getHash()).thenReturn("hash");
        when(itemMock.getNativeName()).thenReturn("name.ext");
        when(ecMock.getResponseOutputStream()).thenReturn(streamMock);

        controller.setId(1L);
        controller.downloadAndSaveItem();
        InOrder inOrder = Mockito.inOrder(fcMock, ecMock, fsMock);
        inOrder.verify(ecMock).responseReset();
        inOrder.verify(ecMock).setResponseContentType("getContentType");
        inOrder.verify(ecMock).setResponseContentLength(10);
        inOrder.verify(ecMock).addResponseHeader("Content-Disposition", "attachment; filename=\"name.ext\"");
        inOrder.verify(fsMock).uploadFile("hash", streamMock);
        inOrder.verify(fcMock).responseComplete();
    }
}