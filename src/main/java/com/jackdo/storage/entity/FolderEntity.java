package com.jackdo.storage.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "folders")
public class FolderEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name")
    String name;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parentFolderId")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private FolderEntity parentFolder;

    public FolderEntity(String name, FolderEntity parentFolder) {
        this.name = name;
        this.parentFolder = parentFolder;
    }

    public FolderEntity() {

    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FolderEntity getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(FolderEntity parentFolder) {
        this.parentFolder = parentFolder;
    }
}
