package com.myapp.stored;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * <p>Created by MontolioV on 16.04.18.
 */
@Entity
@Access(value = AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Tag.GET_ALL, query = "select t from Tag t"),
        @NamedQuery(name = Tag.GET_BY_NAME, query = "select t from Tag t where t.name=:name"),
})
@Table(indexes = {
        @Index(columnList = "name", unique = true),
})
public class Tag implements Serializable {
    private static final String PREFIX = "com.myapp.stored.Tag.";
    public static final String GET_ALL = PREFIX + "GET_ALL";
    public static final String GET_BY_NAME = PREFIX + "GET_BY_NAME";

    private long id;
    private String name;
    private List<Item> items;

    public Tag() {
    }

    public Tag(long id, String name, List<Item> items) {

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
    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return id == tag.id &&
                Objects.equals(name, tag.name) &&
                Objects.equals(items, tag.items);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, items);
    }
}
