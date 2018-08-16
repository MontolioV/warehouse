package com.myapp.storing.UT;

import com.myapp.storing.ItemScheduler;
import com.myapp.storing.ItemStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * <p>Created by MontolioV on 16.08.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemSchedulerTest {
    @InjectMocks
    private ItemScheduler itemScheduler;
    @Mock
    private ItemStore isMock;

    @Test
    public void removeWeekOld() {
        Instant weekAgo = Instant.now().minus(7, DAYS);
        itemScheduler.removeWeekOld();

        ArgumentCaptor<Instant> instantArgumentCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(isMock).deleteOldItemsWithNoOwner(instantArgumentCaptor.capture());
        assertThat(instantArgumentCaptor.getValue(), allOf(
                lessThan(weekAgo.plus(1, MINUTES)),
                greaterThan(weekAgo.minus(1, MINUTES))));
    }
}