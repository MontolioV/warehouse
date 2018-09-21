package com.myapp.storing.UT;

import com.myapp.storing.FetchTagsController;
import com.myapp.storing.Tag;
import com.myapp.storing.TagStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.primefaces.model.tagcloud.TagCloudItem;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchTagsControllerTest {
    @InjectMocks
    private FetchTagsController controller;
    @Mock
    private TagStore tsMock;

    @Test
    public void init() {
        ArrayList<Tag> tags = new ArrayList<>();
        Tag tag = new Tag(0, "tag");
        tag.setLazyItemCounter(1);
        tags.add(tag);
        when(tsMock.fetchMostPopularTags(anyInt())).thenReturn(tags);

        controller.init();
        TagCloudItem tagCloudItem = controller.getTagCloudModel().getTags().get(0);
        assertThat(tagCloudItem.getLabel(), is(tag.getName()));
        assertThat(tagCloudItem.getStrength(), is(5));
        assertThat(tagCloudItem.getUrl(), is("public/items.jsf?tag=tag"));
    }

    @Test
    public void initStrenth() {
        ArrayList<Tag> tags = new ArrayList<>();
        for (int i = 50; i >= 0; i -= 10) {
            Tag tag = new Tag(0, "tag");
            tag.setLazyItemCounter(i);
            tags.add(tag);
        }
        tags.get(0).setName("c");
        tags.get(1).setName("d");
        tags.get(2).setName("e");
        tags.get(3).setName("f");
        tags.get(4).setName("a");
        tags.get(5).setName("b");
        when(tsMock.fetchMostPopularTags(anyInt())).thenReturn(tags);

        controller.init();
        List<TagCloudItem> tagCloudItems = controller.getTagCloudModel().getTags();
        assertThat(tagCloudItems.get(0).getLabel(), is("a"));
        assertThat(tagCloudItems.get(0).getStrength(), is(1));
        assertThat(tagCloudItems.get(1).getLabel(), is("b"));
        assertThat(tagCloudItems.get(1).getStrength(), is(1));
        assertThat(tagCloudItems.get(2).getLabel(), is("c"));
        assertThat(tagCloudItems.get(2).getStrength(), is(5));
        assertThat(tagCloudItems.get(3).getLabel(), is("d"));
        assertThat(tagCloudItems.get(3).getStrength(), is(4));
        assertThat(tagCloudItems.get(4).getLabel(), is("e"));
        assertThat(tagCloudItems.get(4).getStrength(), is(3));
        assertThat(tagCloudItems.get(5).getLabel(), is("f"));
        assertThat(tagCloudItems.get(5).getStrength(), is(2));
    }
}