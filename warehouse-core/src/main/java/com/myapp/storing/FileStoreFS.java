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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Created by MontolioV on 14.05.18.
 */
@Stateless
public class FileStoreFS implements FileStore {
    public static final String PREVIEW_FILENAME_EXTENSION = "." + ImagePreviewMaker.PREVIEW_IMAGE_FORMAT;
    @Inject
    private StorageConfig storageConfig;
    @Inject
    @MD5
    private Hasher hasher;
    @Inject
    private ImagePreviewMaker imagePreviewMaker;

    @Override
    public String persistFile(Part part) throws IOException {
        String hash = hasher.makeHash(part.getInputStream());
        Path resolvedPath = storageConfig.getStorageRoot().resolve(hash);
        if (!Files.exists(resolvedPath)) {
            Files.copy(part.getInputStream(), resolvedPath, StandardCopyOption.REPLACE_EXISTING);
            if (part.getContentType().startsWith("image")) {
                File previewFile = storageConfig.getPreviewRoot().resolve(hash + PREVIEW_FILENAME_EXTENSION).toFile();
                imagePreviewMaker.makePreview(part.getInputStream(), previewFile);
            }
        }
        return hash;
    }

    @Override
    public void uploadFile(String fileHash, OutputStream outputStream) throws IOException {
        Path resolvedPath = storageConfig.getStorageRoot().resolve(fileHash);
        Files.copy(resolvedPath, outputStream);
    }

    @Override
    public Path getPreview(String hash) {
        return storageConfig.getPreviewRoot().resolve(hash + PREVIEW_FILENAME_EXTENSION);
    }

    @Override
    public Set<String> getHashesOfAllStoredFiles() {
        String[] filenames = storageConfig.getStorageRoot().toFile().list();
        return filenames == null ? new HashSet<>() : new HashSet<>(Arrays.asList(filenames));
    }

    @Override
    public long removeFromStorage(String... hash) {
        long result = 0;
        ArrayList<File> filesToDelete = new ArrayList<>();
        Path storageRoot = storageConfig.getStorageRoot();
        Path previewRoot = storageConfig.getPreviewRoot();

        for (String s : hash) {
            filesToDelete.add(storageRoot.resolve(s).toFile());
            filesToDelete.add(previewRoot.resolve(s + PREVIEW_FILENAME_EXTENSION).toFile());
        }
        for (File file : filesToDelete) {
            result += file.length();
            file.delete();
        }
        return result;
    }
}
