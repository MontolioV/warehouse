package com.myapp.storing;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * <p>Created by MontolioV on 16.08.18.
 */
@Singleton
public class ItemScheduler {
    @EJB
    private ItemStore itemStore;

    @Schedule(hour = "*", persistent = false)
    public void removeWeekOld() {
        Instant cutoff = Instant.now().minus(7, ChronoUnit.DAYS);
        itemStore.deleteOldItemsWithNoOwner(cutoff);
    }
}
