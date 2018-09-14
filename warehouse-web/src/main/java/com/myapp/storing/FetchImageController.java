package com.myapp.storing;

import org.omnifaces.cdi.GraphicImageBean;

import javax.ejb.EJB;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@GraphicImageBean
public class FetchImageController {
    @EJB
    private ItemStore itemStore;
    @EJB
    private FileStore fileStore;

    public byte[] getPreviewFromFileItem(long id) throws IOException {
        String hash = ((FileItem) itemStore.getItemById(id)).getHash();
        Path path = fileStore.getPreviewPath(hash);
        return Files.readAllBytes(path);
    }

    public byte[] getImageFromFileItem(long id) throws IOException {
        String hash = ((FileItem) itemStore.getItemById(id)).getHash();
        Path path = fileStore.getFilePath(hash);
        return Files.readAllBytes(path);
    }
}
