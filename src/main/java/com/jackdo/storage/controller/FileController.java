package com.jackdo.storage.controller;

import com.jackdo.storage.entity.FileEntity;
import com.jackdo.storage.entity.FolderEntity;
import com.jackdo.storage.message.ResponseFile;
import com.jackdo.storage.message.ResponseMessage;
import com.jackdo.storage.repo.FileRepo;
import com.jackdo.storage.repo.FolderRepo;
import com.jackdo.storage.service.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/file")
public class FileController {
    FileServiceImpl fileStorageService;
    FolderRepo folderRepo;
    FileRepo fileRepo;

    @Autowired
    public FileController(FileServiceImpl fileStorageService, FolderRepo folderRepo, FileRepo fileRepo) {
        this.fileStorageService = fileStorageService;
        this.folderRepo = folderRepo;
        this.fileRepo = fileRepo;
    }

    @PostMapping("/{folderId}")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("files") List<MultipartFile> files,
            HttpServletRequest req, @PathVariable String folderId) {
        boolean auth = (boolean) req.getAttribute("auth");
        if (!auth) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
            FolderEntity parentFolder = this.folderRepo.findById(UUID.fromString(folderId)).orElse(null);
            if (parentFolder == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            String message = "";
            try {
                fileStorageService.storeFiles(files, parentFolder.getId().toString());
                message = "Uploaded the files successfully";
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            } catch (Exception e) {
                message = "Could not upload the files";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }
    }

    @GetMapping("/{parentFolderId}")
    public ResponseEntity<List<ResponseFile>> getAllFiles(@PathVariable String parentFolderId, HttpServletRequest req) {
        boolean auth = (boolean) req.getAttribute("auth");
        if (!auth) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
            FolderEntity parentFolder = this.folderRepo.findById(UUID.fromString(parentFolderId)).orElse(null);
            if (parentFolder == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            List<ResponseFile> files = this.fileRepo.findAllByParentFolder(parentFolder).stream().map(dbFile -> {
                String fileDownloadUri = "/api/file/download/" + dbFile.getId().toString();
                byte[] data = this.fileStorageService.getData(dbFile.getId().toString());
                return new ResponseFile(dbFile.getName(), fileDownloadUri, dbFile.getType(), dbFile.getId().toString(),
                        data.length);
            }).collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(files);
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> getFileByName(@PathVariable String id, HttpServletRequest req)
            throws SQLException, IOException {
        boolean auth = (boolean) req.getAttribute("auth");
        if (!auth) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
            FileEntity file = fileStorageService.getFile(id);
            byte[] data = fileStorageService.getData(id);
            if (file == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                        .body(data);
            }
        }
    }

    @PutMapping("/rename/{id}")
    public ResponseEntity<Object> renameFile(@PathVariable String id, HttpServletRequest req,
            @RequestParam("name") String name) {
        boolean auth = (boolean) req.getAttribute("auth");
        if (!auth) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
            this.fileStorageService.rename(id, name);
            return ResponseEntity.ok().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteFile(@PathVariable String id, HttpServletRequest req) {
        boolean auth = (boolean) req.getAttribute("auth");
        if (!auth) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
            FileEntity file = fileStorageService.getFile(id);
            if (file == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            fileStorageService.deleteFile(id);
            return ResponseEntity.ok().body(null);
        }
    }
}
