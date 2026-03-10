package com.devsenior.cdiaz.bibliokeep.model.dto;

public record UploadResponseDto(

    String filename,
    String url,
    long size

) {
    
}
