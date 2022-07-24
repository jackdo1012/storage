package com.jackdo.storage.service;

import com.jackdo.storage.entity.FileEntity;
import com.jackdo.storage.entity.FolderEntity;
import com.jackdo.storage.repo.FileRepo;
import com.jackdo.storage.repo.FolderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class FileServiceImpl implements FileService {
    FileRepo fileRepo;
    FolderRepo folderRepo;

    @Autowired
    public FileServiceImpl(FileRepo fileRepo, FolderRepo folderRepo) {
        this.fileRepo = fileRepo;
        this.folderRepo = folderRepo;
    }

    private String getFileName(String initFilename, int initIndex, FolderEntity parentFolder) {
        String fileName = initFilename;
        if (initIndex != 0) {
            int lastIndexOf = fileName.lastIndexOf(".");
            if (lastIndexOf == -1) {
                fileName = initFilename + "-" + initIndex;
            }
            fileName = initFilename.replaceFirst("[.][^.]+$", "") + "-" + initIndex + fileName.substring(lastIndexOf);
        }
        FileEntity checkIfNameExist = fileRepo.findByNameAndParentFolder(fileName, parentFolder);
        if (checkIfNameExist == null) {
            return fileName;
        } else {
            return getFileName(initFilename, initIndex + 1, parentFolder);
        }
    }

    @Override
    public List<FileEntity> storeFiles(List<MultipartFile> files, String folderId) throws IOException {
        FolderEntity parentFolder = this.folderRepo.findById(UUID.fromString(folderId)).orElse(null);
        if (parentFolder == null) {
            return new ArrayList<FileEntity>();
        }
        List<FileEntity> returnFiles = new ArrayList<FileEntity>();
        files.forEach(file -> {
            String fileName = getFileName(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())), 0, parentFolder);
            FileEntity fileEntity = null;
            try {
                fileEntity = new FileEntity(fileName, file.getContentType(), file.getBytes(), parentFolder);
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
    public void deleteFile(String id) {
        fileRepo.deleteById(UUID.fromString(id));
    }
}
