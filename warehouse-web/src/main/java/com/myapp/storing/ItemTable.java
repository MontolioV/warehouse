package com.myapp.storing;

import org.omnifaces.cdi.ViewScoped;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.google.common.collect.Lists.newArrayList;

/**
 * <p>Created by MontolioV on 31.08.18.
 */

@Named
@ViewScoped
public class ItemTable implements Serializable {
    private static final long serialVersionUID = 7736765006928225886L;
    @EJB
    private ItemStore itemStore;
    @EJB
    private TagStore tagStore;
    private List<Item> items;
    private List<Item> filteredItems;
    private List<String> tagNames;
    private List<String> itemTypes;
    private Date fromDate;
    private Date toDate;

    @PostConstruct
    public void init(){
        items = itemStore.getAllAccessibleItems();
        tagNames = tagStore.fetchTagNames();
        itemTypes = newArrayList(TextItem.class.getSimpleName(), FileItem.class.getSimpleName());
    }

    public boolean filterByDate(Object value, Object filter, Locale locale) {
        Date date = (Date) value;
        if (fromDate == null) {
            fromDate = new Date(Long.MIN_VALUE);
        }
        if (toDate == null) {
            toDate = new Date(Long.MAX_VALUE);
        }
        return fromDate.before(date) && toDate.after(date);
    }

    //Setters & Getters

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Item> getFilteredItems() {
        return filteredItems;
    }

    public void setFilteredItems(List<Item> filteredItems) {
        this.filteredItems = filteredItems;
    }

    public List<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }

    public List<String> getItemTypes() {
        return itemTypes;
    }

    public void setItemTypes(List<String> itemTypes) {
        this.itemTypes = itemTypes;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}
