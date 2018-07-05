package com.myapp.storing;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Set;

/**
 * <p>Created by MontolioV on 04.07.18.
 */
public interface FileStore {

    String persistFile(Part part) throws IOException;

    void uploadFile(String fileHash, OutputStream outputStream) throws IOException;

    Path getPreview(String hash);

    Set<String> getHashesOfAllStoredFiles();

    long removeFromStorage(String... hash);
}
