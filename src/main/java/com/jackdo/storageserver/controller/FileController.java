package com.jackdo.storageserver.controller;

import com.jackdo.storageserver.entity.FileEntity;
import com.jackdo.storageserver.message.ResponseFile;
import com.jackdo.storageserver.message.ResponseMessage;
import com.jackdo.storageserver.service.FileStorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FileController {
    FileStorageServiceImpl fileStorageService;

    @Autowired
    public FileController(FileStorageServiceImpl fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/auth")
    public ResponseEntity<Object> auth(HttpServletRequest req) {
        boolean auth = (boolean) req.getAttribute("auth");
        if (auth) {
            return ResponseEntity.ok().body(null);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("files") List<MultipartFile> files,
                                                      HttpServletRequest req) {
        boolean auth = (boolean) req.getAttribute("auth");
        if (!auth) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
            String message = "";
            try {
                fileStorageService.storeFiles(files);
                message = "Uploaded the files successfully";
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            } catch (Exception e) {
                message = "Could not upload the files";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<ResponseFile>> getListFiles(HttpServletRequest req) {
        boolean auth = (boolean) req.getAttribute("auth");
        if (!auth) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
            List<ResponseFile> files = fileStorageService.getAllFiles().map(dbFile -> {
                String fileDownloadUri = ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("/file/")
                        .path(dbFile.getId().toString())
                        .toUriString();
                return new ResponseFile(dbFile.getName(), fileDownloadUri, dbFile.getType(), dbFile.getId().toString(), dbFile.getData().length);
            }).collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(files);
        }
    }

    @GetMapping("/file/{id}")
    public ResponseEntity<byte[]> getFileByName(@PathVariable String id, HttpServletRequest req)
            throws SQLException, IOException {
        boolean auth = (boolean) req.getAttribute("auth");
        System.out.println(auth);
        if (!auth) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
            FileEntity file = fileStorageService.getFile(id);
            if (file == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                        .body(file.getData());
            }
        }
    }

    @DeleteMapping("/file/{id}")
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
