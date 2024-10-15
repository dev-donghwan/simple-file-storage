package com.donghwan.filestoragemodule.file;

public interface File {

    String getUuid();

    String getOriginalFileName();

    String getExtension();

    String getContentType();

    String getStorageType();

    String getStorageSavedPath();

    long getFileSize();

    long getUploadedAt();

    long getModifiedAt();

}
