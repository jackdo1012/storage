package com.jackdo.storage.service;

import com.jackdo.storage.entity.FolderEntity;
import com.jackdo.storage.repo.DataRepo;
import com.jackdo.storage.repo.FileRepo;
import com.jackdo.storage.repo.FolderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FolderServiceImpl implements FolderService {
    private final FolderRepo folderRepo;
    private final FileRepo fileRepo;
    private final DataRepo dataRepo;

    @Autowired
    public FolderServiceImpl(FolderRepo folderRepo, FileRepo fileRepo, DataRepo dataRepo) {
        this.folderRepo = folderRepo;
        this.fileRepo = fileRepo;
        this.dataRepo = dataRepo;
    }

    private String getFolderName(String initFolderName, int initIndex, FolderEntity parentFolder) {
        String folderName = initFolderName;
        if (initIndex != 0) {
            folderName = initFolderName + "-" + initIndex;
        }
        FolderEntity checkIfNameExist = this.folderRepo.findByNameAndParentFolder(folderName, parentFolder);
        if (checkIfNameExist == null) {
            return folderName;
        } else {
            return getFolderName(initFolderName, initIndex + 1, parentFolder);
        }
    }

    @Override
    public FolderEntity createNewFolder(String name, String parentFolderId) {
        FolderEntity parentFolder = this.folderRepo.findById(UUID.fromString(parentFolderId)).orElse(null);
        if (parentFolder == null) {
            return null;
        }
        String folderName = this.getFolderName(name, 0, parentFolder);
        FolderEntity newFolder = new FolderEntity(folderName, parentFolder);
        return this.folderRepo.save(newFolder);
    }

    @Override
    public void deleteFolder(String folderId) {
        this.dataRepo.deleteAllByFile_ParentFolder(this.folderRepo.findById(UUID.fromString(folderId)).orElse(null));
        this.fileRepo.deleteAllByParentFolder(this.folderRepo.findById(UUID.fromString(folderId)).orElse(null));
        this.folderRepo.deleteById(UUID.fromString(folderId));
    }

    @Override
    public void rename(String id, String name) {
        FolderEntity folder = this.folderRepo.findById(UUID.fromString(id)).orElse(null);
        if (folder != null) {
            String folderName = this.getFolderName(name, 0, folder.getParentFolder());
            this.folderRepo.updateNameById(UUID.fromString(id), folderName);
        }
    }
}
