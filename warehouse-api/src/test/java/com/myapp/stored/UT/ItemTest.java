package com.myapp.stored.UT;

import com.myapp.stored.Item;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.myapp.TestUtils.serializationRoutine;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * <p>Created by MontolioV on 16.04.18.
 */
public class ItemTest {

    @Test
    public void serialization() throws IOException, ClassNotFoundException {
        Item item = new Item(1, "test", "test", null, new Date(), true, new ArrayList<>());

        Item restored = (Item) serializationRoutine(item);
        assertThat(restored, is(item));
    }

}