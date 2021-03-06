package com.myapp.storing;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.myapp.storing.Item.*;

/**
 * <p>Created by MontolioV on 16.04.18.
 */
@Entity
@Access(value = AccessType.PROPERTY)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dType")
@NamedQueries({
        @NamedQuery(name = Item.GET_ALL, query = "select i from Item i"),
        @NamedQuery(name = Item.GET_ALL_ACCESSIBLE, query = "select i from Item i where i.shared=true or i.owner=:" + OWNER_PARAM
                                                         +" order by i.id desc"),
        @NamedQuery(name = Item.GET_ALL_OF_CLASS, query = "select i from Item i where type(i)=:" + CLASS_PARAM),
        @NamedQuery(name = Item.GET_LAST_SHARED, query = "select i from Item i where i.shared=true order by i.creationDate desc"),
        @NamedQuery(name = Item.GET_LAST_OF_CLASS, query = "select i from Item i where type(i)=:" + CLASS_PARAM
                                                         + " order by i.creationDate desc"),
        @NamedQuery(name = Item.GET_BY_OWNER, query = "select i from Item i where i.owner=:" + OWNER_PARAM),
        @NamedQuery(name = Item.GET_BY_OWNER_OF_CLASS, query = "select i from Item i where i.owner=:" + OWNER_PARAM
                                                         + " and type(i)=:" + CLASS_PARAM),
        @NamedQuery(name = Item.GET_EXPIRED, query = "select i from Item i where i.owner is null and i.creationDate<:" + MINIMAL_CREATION_DATE_PARAM),

})
@Table(indexes = {
        @Index(columnList = "owner"),
        @Index(columnList = "name"),
        @Index(columnList = "creationDate"),
})
public class Item implements Serializable {
    private static final String PREFIX = "com.myapp.storing.Item.";

    public static final String GET_ALL = PREFIX + "GET_ALL";
    public static final String GET_ALL_ACCESSIBLE = PREFIX + "GET_ALL_ACCESSIBLE";
    public static final String GET_ALL_OF_CLASS = PREFIX + "GET_ALL_OF_CLASS";
    public static final String GET_LAST_SHARED = PREFIX + "GET_LAST_SHARED";
    public static final String GET_LAST_OF_CLASS = PREFIX + "GET_LAST_OF_CLASS";
    public static final String GET_BY_OWNER = PREFIX + "GET_BY_OWNER";
    public static final String GET_BY_OWNER_OF_CLASS = PREFIX + "GET_BY_OWNER_OF_CLASS";
    public static final String GET_EXPIRED = PREFIX + "GET_EXPIRED";

    public static final String OWNER_PARAM = "OWNER_PARAM";
    public static final String CLASS_PARAM = "CLASS_PARAM";
    public static final String MINIMAL_CREATION_DATE_PARAM = "MINIMAL_CREATION_DATE_PARAM";

    private long id;
    private int version;
    private String dType;
    private String name;
    private String description;
    private String owner;
    private Date creationDate;
    private boolean shared = true;
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

    @Version
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Column(name = "dType", insertable = false, updatable = false)
    public String getdType() {
        return dType;
    }

    public void setdType(String dType) {
        this.dType = dType;
    }

    @Size(max = 20)
    @Column(length = 20)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Size(max = 150)
    @Column(length = 150)
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

    @NotNull
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

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @PreRemove
    public void updateTagsLazyCounters() {
        tags.forEach(Tag::decrementLazyItemCounter);
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
