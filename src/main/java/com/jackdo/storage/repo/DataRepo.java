package com.jackdo.storage.repo;

import com.jackdo.storage.entity.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.UUID;

@Repository
public interface DataRepo extends JpaRepository<DataEntity, String> {
    @Transactional
    @Query("delete from DataEntity d where d.file.id = ?1")
    @Modifying
    public void deleteByFileId(UUID fileId);

    @Transactional
    @Query("select d from DataEntity d where d.file.id = ?1")
    public DataEntity findByFileId(UUID fileId);
}
