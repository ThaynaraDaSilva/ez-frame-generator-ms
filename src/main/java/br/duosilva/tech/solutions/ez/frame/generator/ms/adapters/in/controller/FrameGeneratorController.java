package br.duosilva.tech.solutions.ez.frame.generator.ms.adapters.in.controller;

import org.springframework.http.MediaType;
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

	private UploadVideoUseCase uploadVideoUseCase;

	public FrameGeneratorController(UploadVideoUseCase uploadVideoUseCase) {
		super();
		this.uploadVideoUseCase = uploadVideoUseCase;
	}

	@PostMapping(value = "/upload-video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadVideo(@RequestParam("files") MultipartFile[] multipartFiles) {

		uploadVideoUseCase.processUploadedVideo(multipartFiles);
		return ResponseEntity.accepted().build(); // 202 - Aceito para processamento assíncrono
	}

}
