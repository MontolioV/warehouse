package com.myapp.storing.UT;

import com.myapp.storing.FileStore;
import com.myapp.storing.FileStoreCleanerScheduler;
import com.myapp.storing.ItemStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>Created by MontolioV on 05.07.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class FileStoreCleanerSchedulerTest {
    @InjectMocks
    private FileStoreCleanerScheduler fileStoreCleanerScheduler;
    @Mock
    private FileStore fileStore;
    @Mock
    private ItemStore itemStore;
    private Set<String> itemHashes = new HashSet<>();
    private Set<String> fileNames = new HashSet<>();

    @Test
    public void cleanup() {
        itemHashes.add("1");
        itemHashes.add("2");
        fileNames.add("1");
        fileNames.add("2");
        fileNames.add("3");
        fileNames.add("4");
        when(itemStore.getHashesOfFileItems()).thenReturn(itemHashes);
        when(fileStore.getHashesOfAllStoredFiles()).thenReturn(fileNames);
        when(fileStore.removeFromStorage(anyVararg())).thenReturn(1L);

        long bytesCleanup = fileStoreCleanerScheduler.cleanup();
        assertThat(bytesCleanup, is(1L));
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(fileStore).removeFromStorage(captor.capture());
        assertThat(captor.getAllValues().size(), is(2));
        assertTrue(captor.getAllValues().contains("3"));
        assertTrue(captor.getAllValues().contains("4"));
    }
}