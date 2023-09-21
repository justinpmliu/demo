package com.example.demo;


import com.example.demo.util.ZipUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class ZipTest {

    @Test
    void zipFolder() throws IOException {
        ZipUtils.zipFolder("/home/justin/Documents/Picture", "/home/justin/Sandbox/test.zip");
    }

}
