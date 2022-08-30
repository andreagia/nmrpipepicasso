package org.cirmmp.nmrpipepicasso.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

public interface TempDir {

    void createDir(String path);
    Stream<Path> getolddir() throws IOException;
    void deleteDir(Path path);

}
