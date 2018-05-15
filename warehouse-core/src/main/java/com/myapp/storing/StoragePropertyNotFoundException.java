package com.myapp.storing;

/**
 * <p>Created by MontolioV on 15.05.18.
 */
public class StoragePropertyNotFoundException extends Exception {
    public StoragePropertyNotFoundException() {
        super("Check server JVM properties for StorageConfig!");
    }
}
