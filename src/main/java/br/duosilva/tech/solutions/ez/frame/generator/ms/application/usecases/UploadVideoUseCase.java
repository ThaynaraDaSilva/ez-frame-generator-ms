package br.duosilva.tech.solutions.ez.frame.generator.ms.application.usecases;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.duosilva.tech.solutions.ez.frame.generator.ms.domain.service.VideoProcessingService;
import br.duosilva.tech.solutions.ez.frame.generator.ms.domain.service.VideoUploadPolicyService;

@Component
public class UploadVideoUseCase {

	private final VideoProcessingService videoProcessingService;
	private final VideoUploadPolicyService videoUploadPolicyService;

	public UploadVideoUseCase(VideoProcessingService videoProcessingService,
			VideoUploadPolicyService videoUploadPolicyService) {
		this.videoProcessingService = videoProcessingService;
		this.videoUploadPolicyService = videoUploadPolicyService;
	}

	public void processUploadedVideo(MultipartFile[] multipartFiles, String userId) {

		if (multipartFiles == null || multipartFiles.length == 0) {
			throw new IllegalArgumentException("No video to process.");
		} else {

			try {

				for (MultipartFile file : multipartFiles) {
					if (file.isEmpty()) continue;
					
					// 1. Validacoes de regras de negocio (plano Free)
					videoUploadPolicyService.validateFileSize(file);
					videoUploadPolicyService.validateUserDailyUploadLimit(userId);
					

		            // 2. Processamento tecnico do vídeo
		            videoProcessingService.generateFrames(file, userId);
				}

			} catch (Exception e) {

			}

		}

	}

}
