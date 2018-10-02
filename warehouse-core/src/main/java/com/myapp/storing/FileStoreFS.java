package com.myapp.storing;

import com.myapp.security.BanControl;
import com.myapp.utils.Hasher;
import com.myapp.utils.ImagePreviewMaker;
import com.myapp.utils.MD5;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
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

    @BanControl
    @Override
    public String persistFile(TemporaryFileInput temporaryFileInput, @NotNull String contentType) throws IOException {
        String hash = hasher.makeHash(temporaryFileInput.getInputStream());
        Path resolvedPath = storageConfig.getStorageRoot().resolve(hash);
        if (!Files.exists(resolvedPath)) {
            Files.copy(temporaryFileInput.getInputStream(), resolvedPath, StandardCopyOption.REPLACE_EXISTING);
            if (contentType.startsWith("image")) {
                File previewFile = storageConfig.getPreviewRoot().resolve(hash + PREVIEW_FILENAME_EXTENSION).toFile();
                imagePreviewMaker.makePreview(temporaryFileInput.getInputStream(), previewFile);
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
    public Path getFilePath(String hash) {
        return storageConfig.getStorageRoot().resolve(hash);
    }

    @Override
    public Path getPreviewPath(String hash) {
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
