package br.duosilva.tech.solutions.ez.frame.generator.ms.domain.service;

public class VideoUploadPolicyService {

	private static final long MAX_SIZE_FREE_PLAN_BYTES = 100 * 1024 * 1024; // 100MB
	private static final int MAX_UPLOADS_PER_USER_PER_DAY = 10;

	public boolean validateFileSize() {
		return true;
	}

	public boolean validateUserUploadLimit() {
		return true;
	}

}
