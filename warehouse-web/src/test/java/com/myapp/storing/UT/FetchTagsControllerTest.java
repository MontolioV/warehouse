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
        tags.add(tag);
        when(tsMock.fetchMostPopularTags(anyInt())).thenReturn(tags);

        controller.init();
        TagCloudItem tagCloudItem = controller.getTagCloudModel().getTags().get(0);
        assertThat(tagCloudItem.getLabel(), is(tag.getName()));
        assertThat(tagCloudItem.getStrength(), is(tag.getLazyItemCounter()));
        assertThat(tagCloudItem.getUrl(), is("public/items.jsf?tag=tag"));
    }
}