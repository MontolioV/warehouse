package com.myapp.conversion.UT;

import com.myapp.conversion.TagNamesConverter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

/**
 * <p>Created by MontolioV on 28.08.18.
 */
public class TagNamesConverterTest {
    private TagNamesConverter converter = new TagNamesConverter();

    @Test
    public void getAsObject() {
        String s = "TAG 1\r\n \n #TAG    2 \n $ТЕСТ";
        List<String> asObject = converter.getAsObject(null, null, s);
        assertThat(asObject, containsInAnyOrder("tag_1", "#tag_2", "$тест"));
    }

    @Test
    public void getAsString() {
        ArrayList<String> strings = newArrayList("tag1", "tag2", "тест");
        String asString = converter.getAsString(null, null, strings);
        assertThat(asString, is("tag1\ntag2\nтест"));
    }
}