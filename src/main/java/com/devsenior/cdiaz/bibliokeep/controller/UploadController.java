package com.devsenior.cdiaz.bibliokeep.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.devsenior.cdiaz.bibliokeep.model.dto.UploadResponseDto;
import com.devsenior.cdiaz.bibliokeep.service.FileService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/file")
public class UploadController {

    private final FileService fileService;

    @PostMapping("/upload")
    public UploadResponseDto upload(@RequestParam MultipartFile file){

        return fileService.save(file);

    }
    
}
