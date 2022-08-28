package com.jackdo.storage.service;

import com.jackdo.storage.entity.FolderEntity;

public interface FolderService {
    FolderEntity createNewFolder(String name, String parentFolderId);

    void deleteFolder(String folderId);

    void rename(String id, String name);
}
