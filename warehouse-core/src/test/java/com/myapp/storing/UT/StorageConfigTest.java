package com.myapp.storing.UT;

import com.myapp.storing.StorageConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * <p>Created by MontolioV on 15.05.18.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Paths.class, StorageConfig.class})
public class StorageConfigTest {
    private StorageConfig storageConfig;
    @Mock
    private Path rootMock;
    @Mock
    private Path storageMock;
    @Mock
    private Path previewMock;
    @Mock
    private File fileMock;
    private String root = "/";

    @Before
    public void setUp() {
        storageConfig = new StorageConfig();
        storageConfig.setInjectedRootString(root);
        mockStatic(Paths.class);
        when(Paths.get(root)).thenReturn(rootMock);
        when(rootMock.toFile()).thenReturn(fileMock);
        when(storageMock.toFile()).thenReturn(fileMock);
        when(previewMock.toFile()).thenReturn(fileMock);
        when(rootMock.resolve("files")).thenReturn(storageMock);
        when(rootMock.resolve("preview")).thenReturn(previewMock);
    }

    @Test
    public void allExists() {
        when(fileMock.exists()).thenReturn(true);

        storageConfig.init();
        assertThat(storageConfig.getStorageRoot(), is(storageMock));
        assertThat(storageConfig.getPreviewRoot(), is(previewMock));
        verify(fileMock, never()).mkdir();
    }

    @Test(expected = IllegalStateException.class)
    public void dirDoesntExist() {
        when(fileMock.exists()).thenReturn(false);
        storageConfig.init();
    }
}