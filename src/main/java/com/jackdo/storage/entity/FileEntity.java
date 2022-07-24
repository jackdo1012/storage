package com.jackdo.storage.entity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "files")
public class FileEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    private String type;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "data")
    private byte[] data;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "folderId")
    private FolderEntity parentFolder;

    public FileEntity() {
    }

    public FileEntity(String name, String type, byte[] data, FolderEntity parentFolder) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.parentFolder = parentFolder;
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public FolderEntity getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(FolderEntity parentFolder) {
        this.parentFolder = parentFolder;
    }
}
