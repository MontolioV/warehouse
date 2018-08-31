package com.myapp.storing;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Created by MontolioV on 29.08.18.
 */
@FunctionalInterface
public interface TemporaryFileInput {

    InputStream getInputStream() throws IOException;
}
