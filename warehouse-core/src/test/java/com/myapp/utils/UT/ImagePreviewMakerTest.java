package com.myapp.utils.UT;

import com.myapp.utils.ImagePreviewMaker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * <p>Created by MontolioV on 15.05.18.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ImageIO.class, ImagePreviewMaker.class})
public class ImagePreviewMakerTest {
    private ImagePreviewMaker maker = new ImagePreviewMaker();
    @Mock
    private InputStream isMock;
    @Mock
    private File fileMock;
    private BufferedImage srcImage;

    @Test
    public void makePreview() throws IOException {
        testWithParams(400, 240, 200, 120);
        testWithParams(400, 1000, 80, 200);
        testWithParams(199, 198, 199, 198);
    }

    private void testWithParams(int srcWidth, int srcHeight, int newWidth, int newHeight) throws IOException {
        mockStatic(ImageIO.class);
        srcImage = new BufferedImage(srcWidth, srcHeight, TYPE_INT_RGB);
        when(ImageIO.read(isMock)).thenReturn(srcImage);

        maker.makePreview(isMock, fileMock);

        ArgumentCaptor<BufferedImage> captor = ArgumentCaptor.forClass(BufferedImage.class);
        verifyStatic(ImageIO.class);
        ImageIO.write(captor.capture(), eq("jpg"), eq(fileMock));
        BufferedImage captorValue = captor.getValue();
        assertThat(captorValue.getWidth(), is(newWidth));
        assertThat(captorValue.getHeight(), is(newHeight));
    }
}