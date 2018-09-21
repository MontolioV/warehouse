package com.myapp.storing;

import org.primefaces.model.tagcloud.DefaultTagCloudItem;
import org.primefaces.model.tagcloud.DefaultTagCloudModel;
import org.primefaces.model.tagcloud.TagCloudItem;
import org.primefaces.model.tagcloud.TagCloudModel;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import java.util.Comparator;
import java.util.List;

@Model
public class FetchTagsController {
    @EJB
    private TagStore tagStore;
    private TagCloudModel tagCloudModel;
    private int mostPopularTagCounter = 0;

    @PostConstruct
    public void init() {
        tagCloudModel = new DefaultTagCloudModel();
        List<Tag> tags = tagStore.fetchMostPopularTags(20);
        Tag mostPopularTag = tags.get(0);
        if (mostPopularTag == null || mostPopularTag.getLazyItemCounter() < 1) {
            return;
        }
        mostPopularTagCounter = mostPopularTag.getLazyItemCounter();

        tags.forEach(tag -> tagCloudModel.addTag(new DefaultTagCloudItem(
                tag.getName(),
                "public/items.jsf?tag=" + tag.getName(),
                calculateStrenth(tag.getLazyItemCounter()))));
        tagCloudModel.getTags().sort(Comparator.comparing(TagCloudItem::getLabel));

    }

    public TagCloudModel getTagCloudModel() {
        return tagCloudModel;
    }

    private int calculateStrenth(int counter) {
        int round = Math.round((counter * 5) / (float) mostPopularTagCounter);
        return round < 1 ? 1 : round;
    }
}
