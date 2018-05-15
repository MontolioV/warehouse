package com.myapp.storing;

import com.myapp.utils.Hasher;
import com.myapp.utils.ImagePreviewMaker;
import com.myapp.utils.MD5;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * <p>Created by MontolioV on 14.05.18.
 */
@Stateless
public class FileStore {
    @Inject
    private StorageConfig storageConfig;
    @Inject
    @MD5
    private Hasher hasher;
    @Inject
    private ImagePreviewMaker imagePreviewMaker;

    public String persistFile(Part part) throws IOException {
        String hash = hasher.makeHash(part.getInputStream());
        Path resolvedPath = storageConfig.getStorageRoot().resolve(hash);
        if (!Files.exists(resolvedPath)) {
            Files.copy(part.getInputStream(), resolvedPath, StandardCopyOption.REPLACE_EXISTING);
            File previewFile = storageConfig.getPreviewRoot().resolve(hash + ".jpg").toFile();
            imagePreviewMaker.makePreview(part.getInputStream(), previewFile);
        }
        return hash;
    }

    public void uploadFile(String fileHash, OutputStream outputStream) throws IOException {
        Path resolvedPath = storageConfig.getStorageRoot().resolve(fileHash);
        Files.copy(resolvedPath, outputStream);
    }

    public Path getPreview(String hash) {
        return storageConfig.getPreviewRoot().resolve(hash + ".jpg");
    }

    public StorageConfig getStorageConfig() {
        return storageConfig;
    }

    public void setStorageConfig(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
    }

    public Hasher getHasher() {
        return hasher;
    }

    public void setHasher(Hasher hasher) {
        this.hasher = hasher;
    }
}