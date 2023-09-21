package com.example.demo.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class ZipUtils {
    private ZipUtils() {}

    public static void zipFolder(String sourceDir, String zipFileName) throws IOException {
        Files.deleteIfExists(Path.of(zipFileName));
        File zipFile = new File(zipFileName);
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            File fileSource = new File(sourceDir);
            addDirectory(zos, sourceDir, fileSource);
            log.info("Zip file has been created!");
        }
    }

    private static String getRelativePath(String sourceDir, File file) {
        // Trim off the start of source dir path...
        String path = file.getPath().substring(sourceDir.length());
        if (path.startsWith(File.separator)) {
            path = path.substring(1);
        }
        return path.trim();
    }

    private static void addDirectory(ZipOutputStream zos, String sourceDir, File fileSource) throws IOException {
        if (fileSource.isDirectory()) {
            File[] files = fileSource.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        addDirectory(zos, sourceDir, file);
                    } else {
                        addFile(zos, sourceDir, file);
                    }
                }
            }
        }
    }

    private static void addFile(ZipOutputStream zos, String sourceDir, File file) throws IOException {
        String filePath = getRelativePath(sourceDir, file);
        log.info("Adding file " + filePath);
        ZipEntry ze = new ZipEntry(filePath);
        ze.setTime(file.lastModified());
        zos.putNextEntry(ze);

        try (FileInputStream fis = new FileInputStream(file)) {
            int length;
            byte[] buffer = new byte[1024];
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
        }

        zos.closeEntry();
    }
}
