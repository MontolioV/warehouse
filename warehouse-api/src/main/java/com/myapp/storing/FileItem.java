package com.myapp.storing;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.myapp.storing.Item.OWNER_PARAM;

/**
 * <p>Created by MontolioV on 29.04.18.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = FileItem.GET_ALL_HASHES, query = "select distinct fi.hash from FileItem fi"),
        @NamedQuery(name = FileItem.GET_TOTAL_SIZE_BY_OWNER, query = "select sum(fi.size) from FileItem fi where fi.owner=:" + OWNER_PARAM),
})
@Access(value = AccessType.PROPERTY)
@Table(indexes = {
        @Index(columnList = "hash"),
})
public class FileItem extends Item {
    private static final String PREFIX = "com.myapp.storing.FileItem.";
    public static final String GET_ALL_HASHES = PREFIX + "GET_ALL_HASHES";
    public static final String GET_TOTAL_SIZE_BY_OWNER = PREFIX + "GET_TOTAL_SIZE_BY_OWNER";
    public static final int MAX_SIZE_BYTE = 1_000_000_000;

    private String nativeName;
    private String contentType;
    private long size;
    private String hash;

    public FileItem() {
        super();
    }

    public FileItem(long id, String name, String description, String owner, Date creationDate, boolean shared, List<Tag> tags, String nativeName, String contentType, long size, String hash) {
        super(id, name, description, owner, creationDate, shared, tags);
        this.nativeName = nativeName;
        this.contentType = contentType;
        this.size = size;
        this.hash = hash;
    }

    @NotBlank
    @Column(nullable = false)
    public String getNativeName() {
        return nativeName;
    }

    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    @NotBlank
    @Column(nullable = false)
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Max(MAX_SIZE_BYTE)
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @NotBlank
    @Column(nullable = false)
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileItem)) return false;
        if (!super.equals(o)) return false;
        FileItem fileItem = (FileItem) o;
        return size == fileItem.size &&
                Objects.equals(nativeName, fileItem.nativeName) &&
                Objects.equals(contentType, fileItem.contentType) &&
                Objects.equals(hash, fileItem.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nativeName, contentType, size, hash);
    }
}
