package com.donghwan.filestoragemodule.file.storage.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

class LocalFileStorageTest {

    private LocalFileStorage localFileStorage;

    @BeforeEach
    void setUp() {
        localFileStorage = new LocalFileStorage();
    }


    @Nested
    class SaveTests {

        @Test
        void saveFileSuccessfully(@TempDir Path tempDir) throws IOException {
            Path filePath = tempDir.resolve("testfile.txt");
            String content = "This is a test file";
            InputStream inputStream = new ByteArrayInputStream(content.getBytes());

            assertThat(Files.exists(filePath)).isFalse();

            assertDoesNotThrow(() -> localFileStorage.save(filePath, inputStream, content.length()));

            assertThat(Files.exists(filePath)).isTrue();

            String savedContent = Files.readString(filePath);
            assertThat(savedContent).isEqualTo(content);
        }

        @Test
        void saveFileCreatesParentDirectoriesIfMissing(@TempDir Path tempDir) {
            Path filePath = tempDir.resolve("nonexistentDir/testfile.txt");
            String content = "This is a test file";
            InputStream inputStream = new ByteArrayInputStream(content.getBytes());

            assertThat(Files.exists(filePath.getParent())).isFalse();

            assertDoesNotThrow(() -> localFileStorage.save(filePath, inputStream, content.length()));

            assertThat(Files.exists(filePath.getParent())).isTrue();
            assertThat(Files.exists(filePath)).isTrue();
        }

        @Test
        void saveFileThrowsIOException(@TempDir Path tempDir) {
            Path filePath = tempDir.resolve("testfile.txt");
            String content = "This is a test file";
            InputStream inputStream = new ByteArrayInputStream(content.getBytes());

            try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
                mockedFiles.when(() -> Files.notExists(filePath.getParent())).thenReturn(false);
                mockedFiles.when(() -> Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)).thenThrow(IOException.class);

                assertThrows(RuntimeException.class, () -> localFileStorage.save(filePath, inputStream, content.length()));
            }
        }
    }

    @Nested
    class DeleteTests {

        @Test
        void deleteFileSuccessfully(@TempDir Path tempDir) throws IOException {
            Path filePath = tempDir.resolve("testfile.txt");
            Files.createFile(filePath);

            assertThat(Files.exists(filePath)).isTrue();

            assertDoesNotThrow(() -> localFileStorage.delete(filePath));

            assertThat(Files.exists(filePath)).isFalse();
        }

        @Test
        void deleteFileDoesNothingWhenFileDoesNotExist(@TempDir Path tempDir) {
            Path filePath = tempDir.resolve("nonexistentfile.txt");

            assertDoesNotThrow(() -> localFileStorage.delete(filePath));
        }

        @Test
        void deleteFileThrowsIOException(@TempDir Path tempDir) {
            Path filePath = tempDir.resolve("testfile.txt");

            try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
                mockedFiles.when(() -> Files.exists(filePath)).thenReturn(true);
                mockedFiles.when(() -> Files.delete(filePath)).thenThrow(IOException.class);

                assertThrows(RuntimeException.class, () -> localFileStorage.delete(filePath));
            }
        }
    }

    @Nested
    class GetFileTests {

        @Test
        void getFileSuccessfully(@TempDir Path tempDir) throws IOException {
            Path filePath = tempDir.resolve("testfile.txt");
            String content = "This is a test file";
            Files.writeString(filePath, content);

            InputStream inputStream = localFileStorage.getFile(filePath);
            assertThat(inputStream).isNotNull();

            byte[] buffer = new byte[content.length()];
            int bytesRead = inputStream.read(buffer);
            assertThat(bytesRead).isEqualTo(content.length());
            assertThat(content).isEqualTo(new String(buffer));
        }

        @Test
        void getFileFailureWhenFileDoesNotExist(@TempDir Path tempDir) {
            Path filePath = tempDir.resolve("nonexistentfile.txt");

            assertThrows(RuntimeException.class, () -> localFileStorage.getFile(filePath));
        }
    }
}