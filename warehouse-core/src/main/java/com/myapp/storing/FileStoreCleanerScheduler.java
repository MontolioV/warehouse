package com.myapp.storing;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import java.util.Set;

import static com.myapp.security.Roles.Const.ADMIN;

/**
 * <p>Created by MontolioV on 04.07.18.
 */
@Singleton
public class FileStoreCleanerScheduler implements FileStoreCleaner {
    @EJB
    private ItemStore itemStore;
    @EJB
    private FileStore fileStore;

    /**
     * Remove files, that are no longer bind to file items, from file storage.
     * @return freed space in bytes.
     */
    @Schedule(hour = "3", persistent = false)
    @RolesAllowed(ADMIN)
    public long cleanup() {
        Set<String> hashesOfFileItems = itemStore.getHashesOfFileItems();
        Set<String> hashesOfAllStoredFiles = fileStore.getHashesOfAllStoredFiles();
        hashesOfAllStoredFiles.removeAll(hashesOfFileItems);
        return fileStore.removeFromStorage(hashesOfAllStoredFiles.toArray(new String[0]));
    }
}
