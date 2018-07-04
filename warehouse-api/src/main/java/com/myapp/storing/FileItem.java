package com.myapp.storing;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * <p>Created by MontolioV on 29.04.18.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = FileItem.GET_ALL_HASHES, query = "select distinct fi.hash from FileItem fi"),
})
@Access(value = AccessType.PROPERTY)
public class FileItem extends Item {
    private static final String PREFIX = "com.myapp.storing.FileItem.";
    public static final String GET_ALL_HASHES = PREFIX + "GET_ALL_HASHES";
    public static final int MAX_SIZE_BYTE = (int) 1_000_000_000;

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
    public String getNativeName() {
        return nativeName;
    }

    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    @NotBlank
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @NotNull
    @Max(MAX_SIZE_BYTE)
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @NotBlank
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
