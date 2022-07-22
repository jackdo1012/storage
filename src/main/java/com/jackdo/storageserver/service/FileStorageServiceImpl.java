package com.jackdo.storageserver.service;

import com.jackdo.storageserver.entity.FileEntity;
import com.jackdo.storageserver.repo.FileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    FileRepo fileRepo;

    @Autowired
    public FileStorageServiceImpl(FileRepo fileRepo) {
        this.fileRepo = fileRepo;
    }

    private String getFileName(String initFilename, int initIndex) {
        String fileName = initFilename;
        if (initIndex != 0) {
            int lastIndexOf = fileName.lastIndexOf(".");
            if (lastIndexOf == -1) {
                fileName = initFilename + "-" + initIndex;
            }
            fileName = initFilename.replaceFirst("[.][^.]+$", "") + "-" + initIndex + fileName.substring(lastIndexOf);
        }
        FileEntity checkIfNameExist = fileRepo.findByName(fileName);
        if (checkIfNameExist == null) {
            return fileName;
        } else {
            return getFileName(initFilename, initIndex + 1);
        }
    }

    @Override
    public List<FileEntity> storeFiles(List<MultipartFile> files) throws IOException {
        List<FileEntity> returnFiles = new ArrayList<FileEntity>();
        files.forEach(file -> {
            String fileName = getFileName(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())), 0);
            FileEntity fileEntity = null;
            try {
                fileEntity = new FileEntity(fileName, file.getContentType(), file.getBytes());
                returnFiles.add(fileRepo.save(fileEntity));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return returnFiles;
    }

    @Override
    public FileEntity getFile(String id) {
        return fileRepo.findById(UUID.fromString(id)).orElse(null);
    }

    @Override
    public Stream<FileEntity> getAllFiles() {
        return fileRepo.findAll().stream();
    }

    @Override
    public void deleteFile(String id) {
        fileRepo.deleteById(UUID.fromString(id));
    }
}
