package com.myapp.storing;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * <p>Created by MontolioV on 16.04.18.
 */
@Entity
@Access(value = AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Tag.GET_ALL, query = "select t from Tag t"),
        @NamedQuery(name = Tag.GET_BY_NAME, query = "select t from Tag t where t.name=:name"),
        @NamedQuery(name = Tag.GET_LIKE_NAME, query = "select t from Tag t where t.name like concat('%',:name,'%')"),
})
@Table(indexes = {
        @Index(columnList = "name", unique = true),
})
public class Tag implements Serializable {
    private static final String PREFIX = "com.myapp.storing.Tag.";
    public static final String GET_ALL = PREFIX + "GET_ALL";
    public static final String GET_BY_NAME = PREFIX + "GET_BY_NAME";
    public static final String GET_LIKE_NAME = PREFIX + "GET_LIKE_NAME";

    private long id;
    private String name;
    private Set<Item> items = new HashSet<>();

    public Tag() {
    }

    public Tag(long id, String name, Set<Item> items) {
        this.id = id;
        this.name = name;
        this.items = items;
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NotBlank
    @Size(max = 30)
    @Column(unique = true, nullable = false, length = 30)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(mappedBy = "tags")
    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
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

        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return name;
    }
}
