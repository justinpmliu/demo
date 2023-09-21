package com.example.demo;


import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class ZipTest {

    @Test
    void testZip() throws IOException {
        this.zipFolder("/home/justin/Documents/Picture", "/home/justin/Sandbox/test.zip");
    }

    private void zipFolder(String sourceDir, String zipFileName) throws IOException {
        File zipFile = new File(zipFileName);
        if (zipFile.exists()) {
            zipFile.delete();
        }
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            File fileSource = new File(sourceDir);
            addDirectory(zos, sourceDir, fileSource);

            zos.close();
            fos.close();
            System.out.println("Zip file has been created!");
        }
    }

    private String getRelativePath(String sourceDir, File file) {
        // Trim off the start of source dir path...
        String path = file.getPath().substring(sourceDir.length());
        if (path.startsWith(File.separator)) {
            path = path.substring(1);
        }
        return path.trim();
    }

    private void addDirectory(ZipOutputStream zos, String sourceDir, File fileSource) throws IOException {
        if (fileSource.isDirectory()) {
            File[] files = fileSource.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        addDirectory(zos, sourceDir, file);
                    } else {
                        String filePath = getRelativePath(sourceDir, file);
                        System.out.println("Adding file " + filePath);
                        zos.putNextEntry(new ZipEntry(filePath));

                        int length;
                        byte[] buffer = new byte[1024];
                        FileInputStream fis = new FileInputStream(file);
                        while ((length = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, length);
                        }
                        zos.closeEntry();
                        fis.close();
                    }
                }
            }
        }
    }
}
