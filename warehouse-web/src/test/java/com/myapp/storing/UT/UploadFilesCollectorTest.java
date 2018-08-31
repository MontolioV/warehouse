package com.myapp.storing.UT;

import com.myapp.storing.FileItem;
import com.myapp.storing.FileStore;
import com.myapp.storing.TemporaryFileInput;
import com.myapp.storing.UploadFilesCollector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;

import static com.myapp.utils.TestSecurityConstants.PASS_HASH_VALID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 29.08.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class UploadFilesCollectorTest {
    @InjectMocks
    private UploadFilesCollector collector;
    @Mock
    private FacesContext fcMock;
    @Mock
    private FileStore fsMock;
    @Mock
    private UploadedFile uploadedFileMock;
    @Mock
    private FileUploadEvent fuEventMock;
    @Mock
    private InputStream inputStreamMock;
    @Captor
    private ArgumentCaptor<TemporaryFileInput> tfInputCaptor;
    private String fName = "fName";
    private String cType = "cType";
    private long fSize = 10L;

    @Before
    public void setUp() throws Exception {
        when(uploadedFileMock.getContentType()).thenReturn(cType);
        when(uploadedFileMock.getFileName()).thenReturn(fName);
        when(uploadedFileMock.getSize()).thenReturn(fSize);
        when(uploadedFileMock.getInputstream()).thenReturn(inputStreamMock);
        when(fuEventMock.getFile()).thenReturn(uploadedFileMock);
    }

    @Test
    public void fileUpload() throws IOException {
        when(fsMock.persistFile(any(TemporaryFileInput.class), eq(cType))).thenReturn(PASS_HASH_VALID);
        
        collector.fileUpload(fuEventMock);
        FileItem fileItem = collector.getTemporalFileItems().poll();

        assertThat(collector.getTemporalFileItems(), empty());
        assertThat(fileItem, notNullValue());
        assertThat(fileItem.getContentType(), is(cType));
        assertThat(fileItem.getNativeName(), is(fName));
        assertThat(fileItem.getSize(), is(fSize));
        assertThat(fileItem.getHash(), is(PASS_HASH_VALID));
        
        verify(fsMock).persistFile(tfInputCaptor.capture(), eq(cType));
        assertThat(tfInputCaptor.getValue().getInputStream(), is(inputStreamMock));

    }

    @Test
    public void fileUploadTooLarge() {
        when(uploadedFileMock.getSize()).thenReturn((long) FileItem.MAX_SIZE_BYTE + 1);

        collector.fileUpload(fuEventMock);

        verify(fcMock).addMessage(eq("fileInput"), any(FacesMessage.class));
        assertThat(collector.getTemporalFileItems(), empty());
    }

    @Test
    public void fileUploadNPE() {
        when(fuEventMock.getFile()).thenReturn(null);
        collector.fileUpload(fuEventMock);
        assertThat(collector.getTemporalFileItems(), empty());
    }

    @Test
    public void name() throws IOException {
        when(fsMock.persistFile(any(TemporaryFileInput.class), eq(cType))).thenThrow(new IOException());

        collector.fileUpload(fuEventMock);

        verify(fcMock).addMessage(eq("fileInput"), any(FacesMessage.class));
        assertThat(collector.getTemporalFileItems(), empty());
    }

    @Test
    public void reset() {
        collector.fileUpload(fuEventMock);
        collector.reset();
        assertThat(collector.getTemporalFileItems(), empty());
    }
}