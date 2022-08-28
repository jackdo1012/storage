package com.jackdo.storage.repo;

import com.jackdo.storage.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FolderRepo extends JpaRepository<FolderEntity, String> {

    @Query("select (count(f) > 0) from FolderEntity f where f.name = ?1")
    public boolean existsByName(String name);

    @Transactional
    @Query("select f from FolderEntity f where f.parentFolder = ?1")
    public FolderEntity findByParentFolder(FolderEntity parentFolder);

    @Query("select (count(f) > 0) from FolderEntity f where f.name is null")
    public boolean existsByNameIsNull();

    @Transactional
    @Query("select f from FolderEntity f where f.parentFolder is null")
    public FolderEntity findByParentFolderIsNull();

    @Query("select (count(f) > 0) from FolderEntity f where f.id = ?1")
    public boolean existsById(UUID id);

    @Transactional
    @Query("select f from FolderEntity f where f.id = ?1")
    public Optional<FolderEntity> findById(UUID id);

    @Transactional
    @Query("select f from FolderEntity f where f.parentFolder = ?1")
    public List<FolderEntity> findAllByParentFolder(FolderEntity parentFolder);

    @Transactional
    @Modifying
    @Query("delete from FolderEntity f where f.id = ?1")
    public void deleteById(UUID id);

    @Transactional
    @Query("select f from FolderEntity f where f.name = ?1 and f.parentFolder = ?2")
    public FolderEntity findByNameAndParentFolder(String name, FolderEntity parentFolder);

    @Transactional
    @Modifying
    @Query("update FolderEntity f set f.name = ?2 where f.id = ?1")
    public void updateNameById(UUID id, String name);
}
