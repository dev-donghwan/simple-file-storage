package com.donghwan.filestoragemodule.file.storage.impl;

import com.donghwan.filestoragemodule.file.storage.FileStorage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import org.springframework.stereotype.Component;

@Component
public class LocalFileStorage implements FileStorage {

    @Override
    public void save(Path path, InputStream inputStream, long size) {
        try {
            if (Files.notExists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + path, e);
        }
    }

    @Override
    public void delete(Path path) {
        try {
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + path, e);
        }
    }

    @Override
    public InputStream getFile(Path path) {
        try {
            return Files.newInputStream(path, StandardOpenOption.READ);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }
    }
}
