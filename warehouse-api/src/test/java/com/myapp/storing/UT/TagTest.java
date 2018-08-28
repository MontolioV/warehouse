package com.myapp.storing.UT;

import com.myapp.storing.Tag;
import org.junit.Test;

import java.io.IOException;
import java.util.regex.Pattern;

import static com.myapp.TestUtils.serializationRoutine;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * <p>Created by MontolioV on 16.04.18.
 */
public class TagTest {

    @Test
    public void serialization() throws IOException, ClassNotFoundException {
        Tag tag = new Tag(1, "test");

        Tag restored = (Tag) serializationRoutine(tag);
        assertThat(restored, is(tag));
    }

    @Test
    public void namePattern() throws NoSuchMethodException {
        String regexp = Tag.class.getMethod("getName", null)
                .getAnnotation(javax.validation.constraints.Pattern.class).regexp();
        Pattern compile = java.util.regex.Pattern.compile(regexp);
        assertTrue(compile.matcher("test").matches());
        assertTrue(compile.matcher("тест").matches());
        assertTrue(compile.matcher("tag_1!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~").matches());
        assertFalse(compile.matcher("Tag_1!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~").matches());
        assertFalse(compile.matcher("\rtag_1!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~").matches());
        assertFalse(compile.matcher("tag\n_1!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~").matches());
        assertFalse(compile.matcher("tag 1!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~").matches());
    }
}