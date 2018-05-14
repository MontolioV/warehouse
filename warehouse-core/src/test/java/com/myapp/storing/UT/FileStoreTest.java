package com.myapp.storing.UT;

import com.myapp.storing.FileStore;
import com.myapp.storing.StorageConfig;
import com.myapp.utils.Hasher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * <p>Created by MontolioV on 14.05.18.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Files.class, FileStore.class})
public class FileStoreTest {
    @InjectMocks
    private FileStore fileStore;
    @Mock
    private StorageConfig scMock;
    @Mock
    private Hasher hasherMock;
    @Mock
    private Part partMock;
    @Mock
    private Path rootMock;
    @Mock
    private Path pathMock;
    @Mock
    private InputStream isMock;
    @Mock
    private OutputStream osMock;
    private String hash = "hash";

    @Before
    public void setUp() throws Exception {
        fileStore.setHasher(hasherMock);
        fileStore.setStorageConfig(scMock);

        mockStatic(Files.class);
        when(partMock.getInputStream()).thenReturn(isMock);
        when(hasherMock.makeHash(isMock)).thenReturn(hash);
        when(scMock.getStorageRoot()).thenReturn(rootMock);
        when(rootMock.resolve(hash)).thenReturn(pathMock);
    }

    @Test
    public void persistFileNew() throws IOException {
        when(Files.exists(pathMock)).thenReturn(false);
        String s = fileStore.persistFile(partMock);

        assertThat(s, is(hash));
        verifyStatic(Files.class);
        Files.copy(isMock, pathMock, StandardCopyOption.COPY_ATTRIBUTES);
    }

    @Test
    public void persistFileExisting() throws IOException {
        when(Files.exists(pathMock)).thenReturn(true);
        String s = fileStore.persistFile(partMock);

        assertThat(s, is(hash));
        verifyStatic(Files.class, never());
        Files.copy(eq(isMock), eq(pathMock), any(CopyOption.class));
    }

    @Test
    public void findFileByHashSuccess() throws IOException {
        fileStore.uploadFile(hash, osMock);

        verifyStatic(Files.class);
        Files.copy(pathMock, osMock);
    }

    @Test(expected = IOException.class)
    public void findFileByHashFail() throws IOException {
        when(Files.copy(pathMock, osMock)).thenThrow(new IOException());
        fileStore.uploadFile(hash, osMock);
    }
}