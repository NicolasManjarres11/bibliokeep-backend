package com.devsenior.cdiaz.bibliokeep.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.devsenior.cdiaz.bibliokeep.model.dto.UploadResponseDto;
import com.devsenior.cdiaz.bibliokeep.service.FileService;

@Service
public class FileServiceImpl implements FileService {

    private final Path root;

    @Value("${app.upload.path-pattern}")
    private String patternPath;

    public FileServiceImpl(@Value("${app.upload.directory}") String uploadDir) {

        root = Paths.get(uploadDir);
    }

    @Override
    public UploadResponseDto save(MultipartFile file) {


        try {

        // Verificar si la carpeta no existe, crearla

            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

        //Verificar si el archivo no tiene contenido
            
        if(file.isEmpty()){
            throw new RuntimeException("El archivo está vacío");
        }

        var fileName = String.format("%s.webp",  UUID.randomUUID());
        Files.copy(file.getInputStream(),root.resolve(fileName));

        var url = String.format("%s/%s", patternPath, fileName);



        return new UploadResponseDto(fileName, url, file.getSize());

        } catch (IOException e) {
            throw new RuntimeException("Error fatal al guardar un archivo en el disco");
        }

 

    }

}
