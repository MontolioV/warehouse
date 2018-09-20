package com.myapp.storing;

import org.primefaces.model.tagcloud.DefaultTagCloudItem;
import org.primefaces.model.tagcloud.DefaultTagCloudModel;
import org.primefaces.model.tagcloud.TagCloudModel;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import java.util.List;

@Model
public class FetchTagsController {
    @EJB
    private TagStore tagStore;
    private TagCloudModel tagCloudModel;

    @PostConstruct
    public void init() {
        List<Tag> tags = tagStore.fetchMostPopularTags(30);
        tagCloudModel = new DefaultTagCloudModel();
        tags.forEach(tag -> tagCloudModel.addTag(new DefaultTagCloudItem(
                tag.getName(),
                "public/items.jsf?tag=" + tag.getName(),
                tag.getLazyItemCounter())));
    }

    public TagCloudModel getTagCloudModel() {
        return tagCloudModel;
    }
}
