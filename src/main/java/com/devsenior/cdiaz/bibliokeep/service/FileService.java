package com.devsenior.cdiaz.bibliokeep.service;

import org.springframework.web.multipart.MultipartFile;

import com.devsenior.cdiaz.bibliokeep.model.dto.UploadResponseDto;

public interface FileService {
    
    UploadResponseDto save(MultipartFile file);
}
