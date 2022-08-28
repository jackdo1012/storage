package com.jackdo.storage.controller;

import com.jackdo.storage.entity.FolderEntity;
import com.jackdo.storage.repo.FolderRepo;
import com.jackdo.storage.service.FolderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/folder")
public class FolderController {
    private final FolderRepo folderRepo;
    private final FolderServiceImpl folderService;

    @Autowired
    public FolderController(FolderRepo folderRepo, FolderServiceImpl folderService) {
        this.folderRepo = folderRepo;
        this.folderService = folderService;
    }

    @GetMapping("/root")
    public ResponseEntity<FolderEntity> getRootFolderId() {
        FolderEntity root = this.folderRepo.findByParentFolderIsNull();
        return ResponseEntity.ok().body(root);
    }

    @GetMapping("/{parentFolderId}")
    public ResponseEntity<List<FolderEntity>> getFolders(@PathVariable String parentFolderId) {
        FolderEntity parentFolder = this.folderRepo.findById(UUID.fromString(parentFolderId)).orElse(null);
        if (parentFolder == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        List<FolderEntity> folders = this.folderRepo.findAllByParentFolder(parentFolder);
        return ResponseEntity.ok().body(folders);
    }

    @PostMapping("/{parentFolderId}")
    public ResponseEntity<FolderEntity> createNewFolder(@RequestBody String name, @PathVariable String parentFolderId) {
        if (!this.folderRepo.existsById(UUID.fromString(parentFolderId))) {
            return ResponseEntity.badRequest().body(null);
        }
        FolderEntity newFolder = this.folderService.createNewFolder(name, parentFolderId);
        if (newFolder == null) {
            return ResponseEntity.status(500).body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(newFolder);
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<Object> delete(@PathVariable String folderId) {
        if (!this.folderRepo.existsById(UUID.fromString(folderId))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        this.folderService.deleteFolder(folderId);
        return ResponseEntity.ok().body(null);
    }

    @PutMapping("/rename/{id}")
    public ResponseEntity<Object> renameFolder(@PathVariable String id, HttpServletRequest req,
            @RequestParam("name") String name) {
        boolean auth = (boolean) req.getAttribute("auth");
        if (!auth) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
            this.folderService.rename(id, name);
            return ResponseEntity.ok().body(null);
        }
    }
}
