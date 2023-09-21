package com.example.demo;


import com.example.demo.util.ZipUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Slf4j
class ZipTest {

    @Test
    void zipFolder() throws IOException {
        ZipUtils.zipFolder("/home/justin/Documents/Picture", null,"/home/justin/Sandbox/test.zip");
    }


}
