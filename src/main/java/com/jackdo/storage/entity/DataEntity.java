package com.jackdo.storage.entity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "datas")
public class DataEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "fileId")
    private FileEntity file;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "data")
    private byte[] data;

    public DataEntity() {
    }

    public DataEntity(FileEntity file, byte[] data) {
        this.file = file;
        this.data = data;
    }

    public UUID getId() {
        return id;
    }

    public FileEntity getFile() {
        return file;
    }

    public void setFile(FileEntity file) {
        this.file = file;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
