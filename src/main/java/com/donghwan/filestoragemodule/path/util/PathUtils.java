package com.donghwan.filestoragemodule.path.util;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PathUtils {

    /**
     * 운영체제에 상관없이 주어진 경로를 검증하고, 유효한 절대 경로로 변환합니다. Mac, Unix, Linux 계열에서는 /로 시작하고 ~로 시작하면 안되며, Windows에서는 드라이브 문자로 시작해야 합니다.
     *
     * @param inputPath 사용자로부터 입력받은 경로
     * @return 정규화된 절대 경로
     * @throws InvalidPathException 잘못된 경로가 입력되었을 때 발생
     */
    public static Path getNormalizedAbsolutePath(String inputPath) {
        if (inputPath == null || inputPath.isEmpty()) {
            throw new RuntimeException("input path is null or empty");
        }

        if (isWindows()) {
            if (!isWindowOsPathValidation(inputPath)) {
                throw new InvalidPathException(inputPath, "Invalid Windows path format.");
            }
        } else {
            if (!isLinuxOsPathValidation(inputPath)) {
                throw new InvalidPathException(inputPath, "Invalid Unix/Linux path format.");
            }
        }

        return Paths.get(inputPath).normalize().toAbsolutePath();
    }

    /**
     * Windows 경로가 유효한지 검증하는 함수. 경로는 드라이브 문자로 시작해야 하며, ../, ./ 와 같은 상대 경로 참조를 포함해서는 안 됩니다.
     *
     * @param inputPath 사용자로부터 입력받은 경로
     * @return 경로가 유효한 Windows 절대 경로인지 여부
     */
    private static boolean isWindowOsPathValidation(String inputPath) {
        // Windows: 드라이브 문자로 시작 (C:/ 또는 C:\)
        if (!inputPath.matches("^[a-zA-Z]:[/\\\\].*")) {
            return false;
        }

        return !containsInvalidPath(inputPath);
    }

    /**
     * Unix/Linux/Mac 경로가 유효한지 검증하는 함수. 경로는 /로 시작해야 하며, ../, ./ 와 같은 상대 경로 참조 및 ~로 시작하는 경로는 허용하지 않습니다.
     *
     * @param inputPath 사용자로부터 입력받은 경로
     * @return 경로가 유효한 Unix/Linux/Mac 절대 경로인지 여부
     */
    private static boolean isLinuxOsPathValidation(String inputPath) {
        // Unix/Linux/Mac: 경로는 /로 시작해야 함
        if (!inputPath.startsWith("/")) {
            return false;
        }

        // 상대 경로 참조나 홈 디렉토리(~)가 포함되면 안됨
        return !containsInvalidPath(inputPath) && !inputPath.startsWith("~");
    }

    /**
     * 경로에 ../, ./, ~ 등이 포함되었는지 확인합니다.
     *
     * @param inputPath 사용자로부터 입력받은 경로
     * @return 상대 디렉토리 참조가 포함된 경로인지 여부
     */
    private static boolean containsInvalidPath(String inputPath) {
        // 상대 디렉토리 이동과 홈 디렉토리(~) 확인
        return inputPath.contains("../") || inputPath.contains("..\\")
            || inputPath.contains("./") || inputPath.contains(".\\")
            || inputPath.startsWith("~");
    }


    /**
     * 현재 운영체제가 Windows인지 여부를 확인합니다.
     *
     * @return true if current OS is Windows, otherwise false
     */
    private static boolean isWindows() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("win");
    }
}
