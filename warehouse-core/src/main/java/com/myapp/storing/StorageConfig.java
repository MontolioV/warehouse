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
    private Path storageRoot;

    public StorageConfig() throws FileNotFoundException {
        String s = System.getProperty("warehouse.storage");
        storageRoot = Paths.get(s);
        if (!storageRoot.toFile().exists()) {
            throw new FileNotFoundException(storageRoot.toString());
        }
    }

    public Path getStorageRoot() {
        return storageRoot;
    }
}
