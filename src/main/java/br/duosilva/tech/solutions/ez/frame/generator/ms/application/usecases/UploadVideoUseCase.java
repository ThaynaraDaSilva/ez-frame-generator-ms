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
			VideoUploadPolicyService uploadPolicyService) {
		this.videoProcessingService = videoProcessingService;
		this.videoUploadPolicyService = uploadPolicyService;
	}

	public void processUploadedVideo(MultipartFile[] multipartFiles) {

		if (multipartFiles == null || multipartFiles.length == 0) {
			throw new IllegalArgumentException("No video to process.");
		} else {

			System.out.println("################################");
			System.out.println("PROCESS UPLOADED VIDEO");
			System.out.println("################################");

			try {

				for (MultipartFile file : multipartFiles) {
					if (file.isEmpty()) {
						throw new IllegalArgumentException("No video to process.");
					}

					videoProcessingService.generateFrames(file);
				}

			} catch (Exception e) {

			}

		}

	}

}
