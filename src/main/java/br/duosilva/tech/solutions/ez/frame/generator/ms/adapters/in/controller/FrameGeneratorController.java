package br.duosilva.tech.solutions.ez.frame.generator.ms.adapters.in.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.duosilva.tech.solutions.ez.frame.generator.ms.application.usecases.UploadVideoUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController()
@RequestMapping("/v1/ms/frame-generator")
@Tag(name = "Frame Generator Microsservice", description = "Microsserviço responsável por processar vídeos e gerar imagens (frames) automaticamente a partir deles.")
public class FrameGeneratorController {
	
	@Autowired
	private UploadVideoUseCase uploadVideoUseCase;
	
	@PostMapping("/upload-video")
    public ResponseEntity<?> uploadVideo(@RequestParam("files") MultipartFile[] files) {
		 
		uploadVideoUseCase.processUploadedVideo(files);
		return ResponseEntity.accepted().build(); // 202 - Aceito para processamento assíncrono
    }

}
