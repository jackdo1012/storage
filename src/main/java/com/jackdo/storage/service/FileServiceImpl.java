package com.jackdo.storage.service;

import com.jackdo.storage.entity.DataEntity;
import com.jackdo.storage.entity.FileEntity;
import com.jackdo.storage.entity.FolderEntity;
import com.jackdo.storage.repo.DataRepo;
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
    DataRepo dataRepo;

    @Autowired
    public FileServiceImpl(FileRepo fileRepo, FolderRepo folderRepo, DataRepo dataRepo) {
        this.fileRepo = fileRepo;
        this.folderRepo = folderRepo;
        this.dataRepo = dataRepo;
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
            fileEntity = new FileEntity(fileName, file.getContentType(), parentFolder);
            FileEntity newFile = fileRepo.save(fileEntity);
            try {
                DataEntity dataEntity = new DataEntity(newFile, file.getBytes());
                dataRepo.save(dataEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            returnFiles.add(newFile);
        });
        return returnFiles;
    }

    @Override
    public FileEntity getFile(String id) {
        return fileRepo.findById(UUID.fromString(id)).orElse(null);
    }

    @Override
    public void deleteFile(String id) {
        dataRepo.deleteByFileId(UUID.fromString(id));
        fileRepo.deleteById(UUID.fromString(id));
    }

    @Override
    public byte[] getData(String id) {
        DataEntity dataEntity = dataRepo.findByFileId(UUID.fromString(id));
        if (dataEntity == null) {
            return null;
        }
        return dataEntity.getData();
    }

    @Override
    public void rename(String id, String name) {
        FileEntity file = this.fileRepo.findById(UUID.fromString(id)).orElse(null);
        if (file != null) {
            String filename = this.getFileName(name, 0, file.getParentFolder());
            this.fileRepo.updateNameById(UUID.fromString(id), filename);
        }
    }
}
