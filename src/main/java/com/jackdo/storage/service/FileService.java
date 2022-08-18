package com.jackdo.storage.service;

import com.jackdo.storage.entity.FileEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    public List<FileEntity> storeFiles(List<MultipartFile> file, String folderId) throws IOException;

    public FileEntity getFile(String id);

    public void deleteFile(String id);

    public byte[] getData(String id);
}
