package br.duosilva.tech.solutions.ez.frame.generator.ms.domain.policy;

import org.springframework.stereotype.Component;

@Component
public class UploadPolicy {

	public long getMaxFileSizeBytes() {
		return 50 * 1024 * 1024;
	}

	public long getMaxTotalRequestSizeBytes() {
		return 100 * 1024 * 1024;
	}

	public int getMaxFilesPerRequest() {
		return 3;
	}

	public int getMaxUploadsPerDay() {
		return 5;
	}

	public long getMaxDailyTotalSizeBytes() {
		return 300 * 1024 * 1024;
	}

}
