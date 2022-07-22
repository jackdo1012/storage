package com.jackdo.storageserver.service;

import com.jackdo.storageserver.entity.FileEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public interface FileStorageService {
    public List<FileEntity> storeFiles(List<MultipartFile> file) throws IOException;

    public FileEntity getFile(String id);

    public Stream<FileEntity> getAllFiles();

    public void deleteFile(String id);
}
