package com.myapp.storing.UT;

import com.myapp.storing.FileStoreFS;
import com.myapp.storing.StorageConfig;
import com.myapp.utils.Hasher;
import com.myapp.utils.ImagePreviewMaker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;

import static com.myapp.storing.FileStoreFS.PREVIEW_FILENAME_EXTENSION;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * <p>Created by MontolioV on 14.05.18.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Files.class, FileStoreFS.class})
public class FileStoreFSTest {
    @InjectMocks
    private FileStoreFS fileStoreFS;
    @Mock
    private StorageConfig scMock;
    @Mock
    private Hasher hasherMock;
    @Mock
    private ImagePreviewMaker ipmMock;
    @Mock
    private Part partMock;
    @Mock
    private Path storageRootPathMock;
    @Mock
    private Path previewRootPathMock;
    @Mock
    private Path pathMock;
    @Mock
    private File fileMock;
    @Mock
    private File storageRootFileMock;
    @Mock
    private InputStream isMock;
    @Mock
    private OutputStream osMock;
    private String hash = "hash";

    @Before
    public void setUp() throws Exception {
        mockStatic(Files.class);
        when(partMock.getInputStream()).thenReturn(isMock);
        when(hasherMock.makeHash(isMock)).thenReturn(hash);
        when(scMock.getStorageRoot()).thenReturn(storageRootPathMock);
        when(scMock.getPreviewRoot()).thenReturn(previewRootPathMock);
        when(storageRootPathMock.resolve(hash)).thenReturn(pathMock);
        when(previewRootPathMock.resolve(hash + PREVIEW_FILENAME_EXTENSION)).thenReturn(pathMock);
        when(pathMock.toFile()).thenReturn(fileMock);
        when(storageRootPathMock.toFile()).thenReturn(storageRootFileMock);
    }

    @Test
    public void persistFileNew() throws IOException {
        when(Files.exists(pathMock)).thenReturn(false);
        when(partMock.getContentType()).thenReturn("image");
        String s = fileStoreFS.persistFile(partMock);

        assertThat(s, is(hash));
        verifyStatic(Files.class);
        Files.copy(isMock, pathMock, StandardCopyOption.REPLACE_EXISTING);
        verify(ipmMock).makePreview(isMock, fileMock);
    }

    @Test
    public void persistFileNotImage() throws IOException {
        when(Files.exists(pathMock)).thenReturn(false);
        when(partMock.getContentType()).thenReturn("text");
        fileStoreFS.persistFile(partMock);
        verify(ipmMock, never()).makePreview(isMock, fileMock);
    }

    @Test
    public void persistFileExisting() throws IOException {
        when(Files.exists(pathMock)).thenReturn(true);
        String s = fileStoreFS.persistFile(partMock);

        assertThat(s, is(hash));
        verifyStatic(Files.class, never());
        Files.copy(eq(isMock), eq(pathMock), any(CopyOption.class));
        verify(ipmMock, never()).makePreview(any(), any());
    }

    @Test
    public void findFileByHashSuccess() throws IOException {
        fileStoreFS.uploadFile(hash, osMock);

        verifyStatic(Files.class);
        Files.copy(pathMock, osMock);
    }

    @Test(expected = IOException.class)
    public void findFileByHashFail() throws IOException {
        when(Files.copy(pathMock, osMock)).thenThrow(new IOException());
        fileStoreFS.uploadFile(hash, osMock);
    }

    @Test
    public void getPreview() {
        when(previewRootPathMock.resolve(hash + PREVIEW_FILENAME_EXTENSION)).thenReturn(pathMock);
        Path preview = fileStoreFS.getPreview(hash);
        assertThat(preview, is(pathMock));
    }

    @Test
    public void getHashesOfAllStoredFiles() {
        String[] filenames = {"1", "1", "2"};
        when(storageRootFileMock.list()).thenReturn(filenames);

        Set<String> hashesOfAllStoredFiles = fileStoreFS.getHashesOfAllStoredFiles();
        assertThat(hashesOfAllStoredFiles.size(), is(2));
        assertTrue(hashesOfAllStoredFiles.contains("1"));
        assertTrue(hashesOfAllStoredFiles.contains("2"));

        when(storageRootFileMock.list()).thenReturn(null);
        hashesOfAllStoredFiles = fileStoreFS.getHashesOfAllStoredFiles();
        assertTrue(hashesOfAllStoredFiles.isEmpty());
    }

    @Test
    public void removeFromStorage() {
        when(fileMock.length()).thenReturn(1L);
        long removeFromStorageBytes = fileStoreFS.removeFromStorage(hash, hash, hash);
        verify(fileMock, times(6)).delete();
        assertThat(removeFromStorageBytes, is(6L));
    }
}