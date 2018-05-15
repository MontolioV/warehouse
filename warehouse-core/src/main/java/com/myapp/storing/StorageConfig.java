package com.myapp.storing;

import javax.enterprise.context.ApplicationScoped;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Takes storage root from server's system properties for portability.
 * <p>Created by MontolioV on 11.05.18.
 */
@ApplicationScoped
public class StorageConfig {
    private Path root;
    private Path storageRoot;
    private Path previewRoot;

    public StorageConfig() throws FileNotFoundException, StoragePropertyNotFoundException {
        String rootStr = System.getProperty("warehouse.storage");
        if (rootStr == null) {
            throw new StoragePropertyNotFoundException();
        }

        root = Paths.get(rootStr);
        if (!root.toFile().exists()) {
            throw new FileNotFoundException(root.toString());
        }

        storageRoot = root.resolve("files");
        if (!storageRoot.toFile().exists()) {
            storageRoot.toFile().mkdir();
        }

        previewRoot = root.resolve("preview");
        if (!previewRoot.toFile().exists()) {
            previewRoot.toFile().mkdir();
        }
    }

    public Path getStorageRoot() {
        return storageRoot;
    }

    public Path getPreviewRoot() {
        return previewRoot;
    }
}
