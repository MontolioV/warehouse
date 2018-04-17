package com.myapp.storing;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <p>Created by MontolioV on 16.04.18.
 */
@Entity
@Access(value = AccessType.PROPERTY)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dType")
@NamedQueries({
        @NamedQuery(name = Item.GET_ALL, query = "select i from Item i"),
        @NamedQuery(name = Item.GET_ALL_OF_CLASS, query = "select i from Item i where type(i)=:class"),
        @NamedQuery(name = Item.GET_LAST, query = "select i from Item i order by i.creationDate desc"),
        @NamedQuery(name = Item.GET_LAST_OF_CLASS, query = "select i from Item i where type(i)=:class order by i.creationDate desc"),
        @NamedQuery(name = Item.GET_BY_OWNER, query = "select i from Item i where i.owner=:owner"),
        @NamedQuery(name = Item.GET_BY_OWNER_OF_CLASS, query = "select i from Item i where i.owner=:owner and type(i)=:class"),
})
@Table(indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "owner"),
})
public class Item implements Serializable {
    private static final String PREFIX = "com.myapp.storing.Item.";
    public static final String GET_ALL = PREFIX + "GET_ALL";
    public static final String GET_ALL_OF_CLASS = PREFIX + "GET_ALL_OF_CLASS";
    public static final String GET_LAST = PREFIX + "GET_LAST";
    public static final String GET_LAST_OF_CLASS = PREFIX + "GET_LAST_OF_CLASS";
    public static final String GET_BY_OWNER = PREFIX + "GET_BY_OWNER";
    public static final String GET_BY_OWNER_OF_CLASS = PREFIX + "GET_BY_OWNER_OF_CLASS";

    private long id;
    private String dType;
    private String name;
    private String description;
    private String owner;
    private Date creationDate;
    private boolean shared = false;
    private List<Tag> tags = new ArrayList<>();

    public Item() {
    }

    public Item(long id, String name, String description, String owner, Date creationDate, boolean shared, List<Tag> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.creationDate = creationDate;
        this.shared = shared;
        this.tags = tags;
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "dType", insertable = false, updatable = false)
    public String getdType() {
        return dType;
    }

    public void setdType(String dType) {
        this.dType = dType;
    }

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Size(max = 30)
    @Column(length = 30)
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @PastOrPresent
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    @ManyToMany
    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return id == item.id &&
                shared == item.shared &&
                Objects.equals(name, item.name) &&
                Objects.equals(description, item.description) &&
                Objects.equals(owner, item.owner) &&
                Objects.equals(creationDate, item.creationDate) &&
                Objects.equals(tags, item.tags);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, description, owner, creationDate, shared, tags);
    }
}
