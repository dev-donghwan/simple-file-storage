package com.donghwan.filestoragemodule.file.storage;

import java.io.InputStream;
import java.nio.file.Path;

public interface FileStorage {

    void save(Path path, InputStream inputStream, long size);

    void delete(Path path);

    InputStream getFile(Path path);

}
