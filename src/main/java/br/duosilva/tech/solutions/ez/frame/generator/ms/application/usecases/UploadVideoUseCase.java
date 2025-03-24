package br.duosilva.tech.solutions.ez.frame.generator.ms.application.usecases;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.duosilva.tech.solutions.ez.frame.generator.ms.domain.service.VideoProcessingService;
import br.duosilva.tech.solutions.ez.frame.generator.ms.domain.service.VideoUploadPolicyService;
import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.BusinessRuleException;
import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.ErrorMessages;

/**
 * Use case responsável por orquestrar o processo de upload e processamento de
 * vídeos enviados pelo usuário. Aplica regras de negócio e aciona os serviços
 * responsáveis pelo processamento técnico.
 */

@Component
public class UploadVideoUseCase {

	private final VideoProcessingService videoProcessingService;
	private final VideoUploadPolicyService videoUploadPolicyService;

	public UploadVideoUseCase(VideoProcessingService videoProcessingService,
			VideoUploadPolicyService videoUploadPolicyService) {
		this.videoProcessingService = videoProcessingService;
		this.videoUploadPolicyService = videoUploadPolicyService;
	}

	/**
	 * Processa o upload de um ou mais vídeos enviados pelo usuário. Aplica as
	 * regras de negócio (como tamanho do vídeo e limite diário de uploads) e
	 * executa o processamento técnico para extração de frames.
	 *
	 * @param multipartFiles Array de arquivos de vídeo enviados no upload
	 * @param userId         ID do usuário que está realizando o upload
	 * @throws BusinessRuleException se não houver vídeos ou se alguma regra de
	 *                               negócio for violada
	 */
	public void processUploadedVideo(MultipartFile[] multipartFiles, String userId) {

		this.validateFilesPresence(multipartFiles);

		videoUploadPolicyService.validateUserDailyUploadLimit(userId);

		for (MultipartFile file : multipartFiles) {
			if (file.isEmpty())
				continue;

			this.processFile(file, userId);
		}

	}

	private void validateFilesPresence(MultipartFile[] files) {
		if (files == null || files.length == 0) {
			throw new BusinessRuleException(ErrorMessages.NO_VIDEO_PROVIDED);
		}
	}

	private void processFile(MultipartFile file, String userId) {
		videoUploadPolicyService.validateFileSize(file);
		videoProcessingService.generateFrames(file, userId);
	}

}
