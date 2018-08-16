package com.myapp.storing;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
    @Resource(lookup = "java:/strings/storageAddress")
    private String injectedRootString;
    private Path root;
    private Path storageRoot;
    private Path previewRoot;

    @PostConstruct
    public void init() {
        root = Paths.get(injectedRootString);

        if (!root.toFile().exists()) {
            throw new IllegalStateException(new FileNotFoundException(root.toString()));
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

    public void setInjectedRootString(String injectedRootString) {
        this.injectedRootString = injectedRootString;
    }
}
