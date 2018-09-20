package com.myapp.storing;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.myapp.storing.Tag.NAME_PARAM;

/**
 * <p>Created by MontolioV on 16.04.18.
 */
@Entity
@Access(value = AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Tag.GET_ALL, query = "select t from Tag t"),
        @NamedQuery(name = Tag.GET_BY_NAME, query = "select t from Tag t where t.name=:" + NAME_PARAM),
        @NamedQuery(name = Tag.GET_MOST_POPULAR, query = "select t from Tag t order by t.lazyItemCounter desc"),
        @NamedQuery(name = Tag.GET_LIKE_NAME, query = "select t from Tag t where t.name like concat('%',:" + NAME_PARAM + ",'%') " +
                                                      "order by t.name desc"),
})
@Table(indexes = {
        @Index(columnList = "name", unique = true),
        @Index(columnList = "lazyItemCounter"),
})
public class Tag implements Serializable {
    private static final String PREFIX = "com.myapp.storing.Tag.";
    public static final String GET_ALL = PREFIX + "GET_ALL";
    public static final String GET_BY_NAME = PREFIX + "GET_BY_NAME";
    public static final String GET_MOST_POPULAR = PREFIX + "GET_MOST_POPULAR";
    public static final String GET_LIKE_NAME = PREFIX + "GET_LIKE_NAME";
    public static final String NAME_PARAM = "NAME_PARAM";

    private long id;
    private int version;
    private String name;
    private Set<Item> items = new HashSet<>();
    private int lazyItemCounter = 0;

    public Tag() {
    }

    public Tag(long id, String name) {
        this.id = id;
        this.name = name;
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

    @NotBlank
    @Size(max = 30)
    @Pattern(regexp = "[\\d\\p{javaLowerCase}\\p{Punct}]*")
    @Column(nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    @ManyToMany(mappedBy = "tags")
    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
        updateLazyItemCounter();
    }

    @PositiveOrZero
    public int getLazyItemCounter() {
        return lazyItemCounter;
    }

    public void setLazyItemCounter(int itemsCount) {
        this.lazyItemCounter = itemsCount;
    }

    public void updateLazyItemCounter() {
        lazyItemCounter = items.size();
    }

    public void decrementLazyItemCounter() {
        lazyItemCounter--;
    }

    @PreRemove
    public void removeThisTagFromItems() {
        items.forEach(item -> item.getTags().remove(this));
    }

    /**
     * Tags are equal by name only
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return id == tag.id &&
                Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
