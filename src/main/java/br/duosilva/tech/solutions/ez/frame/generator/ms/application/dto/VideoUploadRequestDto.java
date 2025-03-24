package br.duosilva.tech.solutions.ez.frame.generator.ms.application.dto;

import org.springframework.web.multipart.MultipartFile;

public class VideoUploadRequestDto {
	
	private MultipartFile[] files;

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }

}
