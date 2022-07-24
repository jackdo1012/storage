package com.jackdo.storage;

import com.jackdo.storage.entity.FolderEntity;
import com.jackdo.storage.repo.FolderRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StorageApplication {
    private final FolderRepo folderRepo;

    public StorageApplication(FolderRepo folderRepo) {
        this.folderRepo = folderRepo;
    }

    public static void main(String[] args) {
        SpringApplication.run(StorageApplication.class, args);
    }

    @Bean
    public CommandLineRunner CommandLineRunnerBean() {
        return (args) -> {
            if (!this.folderRepo.existsByNameIsNull()) {
                FolderEntity folderEntity = new FolderEntity(null, null);
                this.folderRepo.save(folderEntity);
            }
        };
    }
}
