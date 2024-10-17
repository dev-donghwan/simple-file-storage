package com.donghwan.filestoragemodule.path.util;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PathUtilsTest {

    @Nested
    @EnabledOnOs(OS.WINDOWS) // Windows에서만 실행
    class WindowsPathValidationTests {

        @Test
        void validWindowsPath() {
            String inputPath = "C:/Users/Test/file.txt";
            Path result = PathUtils.getNormalizedAbsolutePath(inputPath);
            assertEquals("C:/Users/Test/file.txt", result.toString());
        }

        @Test
        void invalidWindowsPathWithoutDriveLetter() {
            String inputPath = "/Users/Test/file.txt";
            assertThrows(InvalidPathException.class, () -> PathUtils.getNormalizedAbsolutePath(inputPath));
        }

        @Test
        void invalidWindowsPathWithRelativeReference() {
            String inputPath = "C:/Users/../Test/file.txt";
            assertThrows(InvalidPathException.class, () -> PathUtils.getNormalizedAbsolutePath(inputPath));
        }

        @Test
        void invalidWindowsPathWithCurrentDirectoryReference() {
            String inputPath = "C:/Users/./Test/file.txt";
            assertThrows(InvalidPathException.class, () -> PathUtils.getNormalizedAbsolutePath(inputPath));
        }
    }

    @Nested
    @EnabledOnOs({OS.LINUX, OS.MAC}) // Unix/Linux/Mac에서만 실행
    class UnixLinuxMacPathValidationTests {

        @Test
        void validUnixPath() {
            String inputPath = "/usr/local/bin";
            Path result = PathUtils.getNormalizedAbsolutePath(inputPath);
            assertEquals("/usr/local/bin", result.toString());
        }

        @Test
        void invalidUnixPathWithRelativeReference() {
            String inputPath = "../usr/local/bin";
            assertThrows(InvalidPathException.class, () -> PathUtils.getNormalizedAbsolutePath(inputPath));
        }

        @Test
        void invalidUnixPathWithHomeDirectoryReference() {
            String inputPath = "~/Documents/file.txt";
            assertThrows(InvalidPathException.class, () -> PathUtils.getNormalizedAbsolutePath(inputPath));
        }

        @Test
        void invalidUnixPathWithCurrentDirectoryReference() {
            String inputPath = "/usr/local/./bin";
            assertThrows(InvalidPathException.class, () -> PathUtils.getNormalizedAbsolutePath(inputPath));
        }
    }

    @Nested
    class GeneralTests {

        @Test
        void nullPathShouldThrowException() {
            assertThrows(RuntimeException.class, () -> PathUtils.getNormalizedAbsolutePath(null));
        }

        @Test
        void emptyPathShouldThrowException() {
            assertThrows(RuntimeException.class, () -> PathUtils.getNormalizedAbsolutePath(""));
        }

        @Test
        void validTempDirPathTest(@TempDir Path tempDir) {
            // Create a file path inside a temporary directory (valid path)
            String inputPath = tempDir.resolve("testfile.txt").toString();
            Path resultPath = PathUtils.getNormalizedAbsolutePath(inputPath);
            assertEquals(inputPath, resultPath.toString());
        }
    }
}