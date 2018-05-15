package com.myapp.storing.UT;

import com.myapp.storing.FetchImageController;
import com.myapp.storing.FileItem;
import com.myapp.storing.FileStore;
import com.myapp.storing.ItemStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * <p>Created by MontolioV on 15.05.18.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Files.class, FetchImageController.class})
public class FetchImageControllerTest {
    @InjectMocks
    private FetchImageController controller;
    @Mock
    private ItemStore isMock;
    @Mock
    private FileStore fsMock;
    @Mock
    private FileItem itemMock;
    @Mock
    private Path pathMock;

    @Test
    public void getImageFromFileItem() throws IOException {
        byte[] bytes = new byte[0];
        mockStatic(Files.class);
        when(isMock.getItemById(anyLong(), anyString())).thenReturn(itemMock);
        when(itemMock.getHash()).thenReturn("");
        when(fsMock.getPreview(anyString())).thenReturn(pathMock);
        when(Files.readAllBytes(pathMock)).thenReturn(bytes);

        byte[] imageFromFileItem = controller.getImageFromFileItem(anyLong(), anyString());
        assertThat(imageFromFileItem, sameInstance(bytes));
    }
}