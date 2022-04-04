package org.cirmmp.nmrpipepicasso.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.stream.Stream;

@Service
public class TempDirImpl implements TempDir{
    private static final Logger LOGGER = LoggerFactory.getLogger(TempDirImpl.class);

    @Autowired
    FilesStorageService filesStorageService;

    @Override
    public void createDir(String tmpdir) {
        try {
            Path path = Files.createTempDirectory(tmpdir);
           LOGGER.info(path.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Stream<Path> getolddir() throws IOException {
        Path path = filesStorageService.getRoot();
        final int daydiff = 1;
        LocalDate today = LocalDate.now();
        try (Stream<Path> stream = Files.walk(path, 1)) {
            return stream
                    .filter(file -> Files.isDirectory(file))
                    .filter(e -> {
                        try {
                            FileTime creationTime =
                                    (FileTime) Files.getAttribute(e.getFileName(), "creationTime");
                            LocalDate ldt = LocalDate.ofInstant(creationTime.toInstant(), ZoneId.systemDefault());
                            Period age = Period.between(ldt, today);
                            int days = age.getDays();
                            return days > daydiff;
                        } catch (IOException a) {
                            a.printStackTrace();
                            return false;
                        }
                    });
        }
    }


    @Override
    public void deleteDir(Path path) {
        if (!Files.exists(path)) {
            return;
        }
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                        throws IOException {
                    Files.deleteIfExists(dir);
                    return super.postVisitDirectory(dir, exc);
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    Files.deleteIfExists(file);
                    return super.visitFile(file, attrs);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
