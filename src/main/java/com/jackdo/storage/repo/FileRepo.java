package com.jackdo.storage.repo;

import com.jackdo.storage.entity.FileEntity;
import com.jackdo.storage.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository()
public interface FileRepo extends JpaRepository<FileEntity, String> {
    @Query("select f from FileEntity f")
    @Transactional
    public List<FileEntity> findAll();

    @Query("select f from FileEntity f where f.parentFolder = ?1")
    @Transactional
    public List<FileEntity> findAllByParentFolder(FolderEntity parentFolder);

    @Query("select f from FileEntity f where f.id = ?1")
    @Transactional
    public Optional<FileEntity> findById(UUID id);

    @Query("select f from FileEntity f where f.name = ?1")
    @Transactional
    public FileEntity findByName(String name);

    @Transactional
    @Modifying
    @Query("delete from FileEntity f where f.id = ?1")
    public void deleteById(UUID id);

    @Transactional
    @Query("select f from FileEntity f where f.name = ?1 and f.parentFolder = ?2")
    public FileEntity findByNameAndParentFolder(String name, FolderEntity parentFolder);

    @Transactional
    @Modifying
    @Query("delete from FileEntity f where f.parentFolder = ?1")
    public void deleteAllByParentFolder(FolderEntity parentFolder);
}
