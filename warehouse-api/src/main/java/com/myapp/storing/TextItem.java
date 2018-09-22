package com.myapp.storing;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <p>Created by MontolioV on 17.04.18.
 */
@Entity
@Access(value = AccessType.PROPERTY)
public class TextItem extends Item {
    private String text;

    public TextItem() {
    }

    public TextItem(long id, String name, String description, String owner, Date creationDate, boolean shared, List<Tag> tags, @Size(max = 100_000) String text) {
        super(id, name, description, owner, creationDate, shared, tags);
        this.text = text;
    }

    @Lob()
    @Basic(fetch = FetchType.LAZY)
    @Size(max = 100_000)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextItem)) return false;
        if (!super.equals(o)) return false;
        TextItem textItem = (TextItem) o;
        return Objects.equals(text, textItem.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }
}
