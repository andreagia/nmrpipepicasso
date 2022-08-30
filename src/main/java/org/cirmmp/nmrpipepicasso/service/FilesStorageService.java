package org.cirmmp.nmrpipepicasso.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FilesStorageService {
    public void init();
    public void save(MultipartFile file, String sessid);
    public void savecsv(MultipartFile file, String sessid);
    public Resource load(String filename, String sessid);
    public void deleteAll(String sessid);
    public  void deleteFile(String filename,String sessid);
    public Stream<Path> loadAll(String Sessid);
    public Path getRoot();
}
