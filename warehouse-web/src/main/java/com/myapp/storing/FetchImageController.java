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

    public byte[] getImageFromFileItem(long id, String userName) throws IOException {
        String hash = ((FileItem) itemStore.getItemById(id, userName)).getHash();
        Path path = fileStore.getPreview(hash);
        return Files.readAllBytes(path);
    }
}
