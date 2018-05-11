package com.myapp.storing;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>Created by MontolioV on 29.04.18.
 */
@Entity
@Access(value = AccessType.PROPERTY)
public class FileItem extends Item {
    public static final int MAX_SIZE_BYTE = 100_000_000;

    private String nativeName;
    private String contentType;
    private long size;
    private String hash;
//    private byte[] binaryData;

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

    //    @Lob()
//    @Basic(fetch = FetchType.LAZY)
//    @Column(length = MAX_SIZE_BYTE)
//    public byte[] getBinaryData() {
//        return binaryData;
//    }
//
//    public void setBinaryData(byte[] content) {
//        this.binaryData = content;
//    }
}
