package com.example.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class ZipUtils {
    private ZipUtils() {}

    public static void zipFolder(String baseDir, List<String> subDirs, String zipFileName) throws IOException {
        Files.deleteIfExists(Path.of(zipFileName));
        File zipFile = new File(zipFileName);
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            File fileSource = new File(baseDir);
            addDirectory(zos, baseDir, subDirs, fileSource);
            log.info("Zip file [{}] has been created!", zipFileName);
        }
    }

    private static String getRelativePath(String baseDir, File file) {
        // Trim off the start of source dir path...
        String path = file.getPath().substring(baseDir.length());
        if (path.startsWith(File.separator)) {
            path = path.substring(1);
        }
        return path.trim();
    }

    private static void addDirectory(ZipOutputStream zos, String baseDir, List<String> subDirs, File fileSource) throws IOException {
        if (fileSource.isDirectory()) {
            File[] files = fileSource.listFiles();
            if (files != null) {
                Arrays.sort(files);
                for (File file : files) {
                    if (file.isDirectory()) {
                        String relativePath = getRelativePath(baseDir, file);
                        if (CollectionUtils.isEmpty(subDirs) || isSubDirMatched(relativePath, subDirs, true)) {
                            log.debug("Add directory {}", relativePath);
                            ZipEntry ze = new ZipEntry(relativePath + File.separator);
                            ze.setTime(file.lastModified());
                            zos.putNextEntry(ze);
                            zos.closeEntry();
                            addDirectory(zos, baseDir, subDirs, file);
                        }
                    } else {
                        addFile(zos, baseDir, subDirs, file);
                    }
                }
            }
        }
    }

    private static void addFile(ZipOutputStream zos, String baseDir, List<String> subDirs, File file) throws IOException {
        String relativePath = getRelativePath(baseDir, file);
        if (CollectionUtils.isEmpty(subDirs) || isSubDirMatched(relativePath, subDirs, false)) {
            log.debug("Adding file {}", relativePath);
            ZipEntry ze = new ZipEntry(relativePath);
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

    private static boolean isSubDirMatched(String filePath, List<String> subDirs, boolean isDirectory) {
        boolean isMatched = false;
        for (String subDir : subDirs) {
            if (isDirectory) {
                if (filePath.contains(subDir)) {
                    isMatched = true;
                }
            } else {
                if (filePath.contains(subDir + File.separator)) {
                    isMatched = true;
                }
            }
            if (isMatched) {
                break;
            }
        }
        return isMatched;
    }
}
