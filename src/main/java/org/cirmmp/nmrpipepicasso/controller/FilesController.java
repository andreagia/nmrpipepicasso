package org.cirmmp.nmrpipepicasso.controller;

import org.cirmmp.nmrpipepicasso.message.ResponseMessage;
import org.cirmmp.nmrpipepicasso.model.FileInfo;
import org.cirmmp.nmrpipepicasso.service.FilesStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

//https://www.bezkoder.com/spring-boot-file-upload/
@Controller

public class FilesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesController.class);
    @Autowired
    FilesStorageService storageService;

    @CrossOrigin
    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("sessid") String sessid) {
        String message = "";
        LOGGER.info("Sono in Upload============================================================");
        LOGGER.info(sessid);
        try {
            storageService.save(file,sessid);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }
    @CrossOrigin
    @PostMapping("/uploadcsv")
    public ResponseEntity<ResponseMessage> uploadFilecsv(@RequestParam("file") MultipartFile file, @RequestParam("sessid") String sessid) {
        String message = "";
        LOGGER.info("Sono in Upload CSV ============================================================");
        LOGGER.info(sessid);
        try {
            storageService.savecsv(file,sessid);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }
    @CrossOrigin
    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles(@RequestParam("sessid") String sessid) {
        LOGGER.info(sessid);
        List<FileInfo> fileInfos = storageService.loadAll(sessid).map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "getFile", sessid, path.getFileName().toString()).build().toString();
            return new FileInfo(filename, url);
        }).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }
    @CrossOrigin
    @GetMapping("/files/{sessid}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String sessid, @PathVariable String filename) {
        LOGGER.info(sessid);
        Resource file = storageService.load(filename, sessid);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}