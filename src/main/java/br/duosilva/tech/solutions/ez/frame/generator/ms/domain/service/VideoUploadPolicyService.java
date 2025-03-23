package br.duosilva.tech.solutions.ez.frame.generator.ms.domain.service;

import java.util.Arrays;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.duosilva.tech.solutions.ez.frame.generator.ms.domain.policy.VideoUploadPolicy;
import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.BusinessRuleException;
import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.ErrorMessages;

@Service
public class VideoUploadPolicyService {

	private final VideoUploadPolicy uploadPolicy;

	public VideoUploadPolicyService(VideoUploadPolicy uploadPolicy) {
		this.uploadPolicy = uploadPolicy;
	}

	public void validateFileSize(MultipartFile file) {
		if (file.getSize() > uploadPolicy.getMaxFileSizeBytes()) {
			throw new BusinessRuleException(ErrorMessages.FILE_SIZE_EXCEEDED_FREE_PLAN);
		}
	}

	public void validateUserDailyUploadLimit(String userId) {
		int uploadsToday = 1; // Placeholder — depois será puxado do repositório

		if (uploadsToday >= uploadPolicy.getMaxUploadsPerDay()) {
			throw new BusinessRuleException(ErrorMessages.UPLOAD_LIMIT_EXCEEDED_FREE_PLAN);
		}
	}

	public void validateMaxFilesPerRequest(MultipartFile[] files) {
		int nonEmptyFiles = (int) Arrays.stream(files).filter(file -> !file.isEmpty()).count();

		if (nonEmptyFiles > uploadPolicy.getMaxFilesPerRequest()) {
			throw new BusinessRuleException(ErrorMessages.MAX_FILES_PER_REQUEST_EXCEEDED);
		}
	}

	public void validateTotalSizePerRequest(MultipartFile[] files) {
		long totalSize = Arrays.stream(files).filter(file -> !file.isEmpty()).mapToLong(MultipartFile::getSize).sum();

		if (totalSize > uploadPolicy.getMaxTotalRequestSizeBytes()) {
			throw new BusinessRuleException(ErrorMessages.TOTAL_SIZE_EXCEEDED_FREE_PLAN);
		}
	}
}
