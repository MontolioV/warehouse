package com.myapp.storing;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Set;

/**
 * <p>Created by MontolioV on 04.07.18.
 */
public interface FileStore {

    String persistFile(TemporaryFileInput temporaryFileInput, @NotNull String contentType) throws IOException;

    void uploadFile(String fileHash, OutputStream outputStream) throws IOException;

    Path getFilePath(String hash);

    Path getPreviewPath(String hash);

    Set<String> getHashesOfAllStoredFiles();

    long removeFromStorage(String... hash);
}
