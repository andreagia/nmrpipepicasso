package org.cirmmp.nmrpipepicasso.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {
    //private final Path root = Paths.get("uploads");
    private static final Logger LOGGER = LoggerFactory.getLogger(FilesStorageServiceImpl.class);

    @Value("${directory.tmp}")
    private String tmpdir;
    Path root;

    @Override
    @PostConstruct
    public void init() {
            File dirtmp = new File(this.tmpdir );
            if (!dirtmp.exists()) dirtmp.mkdirs();
            this.root = dirtmp.toPath();
            //this.root = Files.createTempDirectory(this.tmpdir);
            //Files.createDirectory(root);
            LOGGER.info("Directory tmp created on init-method -> " + root.toString());
    }

    @Override
    public Path getRoot() {
        return this.root;
    }

    @Override
    public void save(MultipartFile file, String sid) {

        Path sesspath = this.root.resolve(Paths.get(sid));
        Path sesspathi =sesspath.resolve(Paths.get("input"));

        LOGGER.info(sesspath.toString());
        if (!sesspath.toFile().exists()) sesspath.toFile().mkdirs();
        if (!sesspathi.toFile().exists()) sesspathi.toFile().mkdirs();
        try {
            Files.copy(file.getInputStream(), sesspathi.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Override
    public void savecsv(MultipartFile file, String sid) {

        LOGGER.info(file.getOriginalFilename());
        Path sesspath = this.root.resolve(Paths.get(sid));
        Path sesspathcsv =sesspath.resolve(Paths.get("csv"));

        LOGGER.info(sesspath.toString());
        if (!sesspath.toFile().exists()) sesspath.toFile().mkdirs();
        if (!sesspathcsv.toFile().exists()) sesspathcsv.toFile().mkdirs();
        try {
            Files.copy(file.getInputStream(), sesspathcsv.resolve("ref.csv"));
            //Files.copy(file.getInputStream(), sesspathcsv.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }
    @Override
    public Resource load(String filename, String sid) {

        try {
            Path sesspath = this.root.resolve(Paths.get(sid));
            LOGGER.info(sesspath.toString());
            if (!sesspath.toFile().exists()) sesspath.toFile().mkdirs();
            Path sesspathi =sesspath.resolve(Paths.get("input"));
            if (!sesspathi.toFile().exists()) sesspathi.toFile().mkdirs();
            Path file = sesspathi.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll(String sid) {
        Path sesspath = this.root.resolve(Paths.get(sid));
        if (!sesspath.toFile().exists()) sesspath.toFile().mkdirs();
        Path sesspathi =sesspath.resolve(Paths.get("input"));
        if (!sesspathi.toFile().exists()) sesspathi.toFile().mkdirs();
        LOGGER.info(sesspathi.toString());
        FileSystemUtils.deleteRecursively(sesspathi.toFile());
    }

    @Override
    public void deleteFile(String filename, String sid) {
        Path sesspath = this.root.resolve(Paths.get(sid));
        if (!sesspath.toFile().exists()) sesspath.toFile().mkdirs();
        Path sesspathi =sesspath.resolve(Paths.get("input"));
        if (!sesspathi.toFile().exists()) sesspathi.toFile().mkdirs();
        LOGGER.info(sesspathi.toString());
        FileSystemUtils.deleteRecursively(new File(sesspathi.toFile(), filename));
    }

    @Override
    public Stream<Path> loadAll(String sid) {
        Path sesspath = this.root.resolve(Paths.get(sid));
        if (!sesspath.toFile().exists()) sesspath.toFile().mkdirs();
        Path sesspathi =sesspath.resolve(Paths.get("input"));
        if (!sesspathi.toFile().exists()) sesspathi.toFile().mkdirs();
        LOGGER.info(sesspathi.toString());
        if (sesspath != null) {
        try {
            return Files.walk(sesspathi, 1).filter(path -> !path.equals(sesspathi)).map(sesspathi::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }}else {
            return Stream.of();
        }
    }
}
