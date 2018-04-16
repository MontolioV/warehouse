package com.myapp.stored.UT;

import com.myapp.stored.Tag;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static com.myapp.TestUtils.serializationRoutine;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * <p>Created by MontolioV on 16.04.18.
 */
public class TagTest {

    @Test
    public void serialization() throws IOException, ClassNotFoundException {
        Tag tag = new Tag(1, "test", new ArrayList<>());

        Tag restored = (Tag) serializationRoutine(tag);
        assertThat(restored, is(tag));
    }
}