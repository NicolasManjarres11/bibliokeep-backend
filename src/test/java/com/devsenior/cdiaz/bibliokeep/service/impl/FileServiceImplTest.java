package com.devsenior.cdiaz.bibliokeep.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import com.devsenior.cdiaz.bibliokeep.service.FileService;

public class FileServiceImplTest {

    @TempDir
    Path tempDir;

    private FileService fileService;

    @BeforeEach
    public void setUp(){
        fileService = new FileServiceImpl(tempDir.toString());
    }

    @Test
    void testSaveFileReturnsResponse(){

        var file = new MockMultipartFile(
            "file", 
            "file-test.jpg", 
            "image/jpg", 
            "content".getBytes());

            var response = fileService.save(file);

        assertNotNull(response);
        assertNotNull(response.filename());
        assertTrue(response.filename().endsWith(".webp"));
        assertTrue(tempDir.resolve(response.filename()).toFile().exists());
    }

    @Test
    void testEmptyFileThrowsException() {
        var emptyFile = new MockMultipartFile("file",new byte[0]);

        assertThrows(RuntimeException.class,() -> {
            fileService.save(emptyFile);
        });
    }
}
