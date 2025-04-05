package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.S3;

import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.BusinessRuleException;

public class S3KeyGenerator {

	public static String generateZipKey(String userId, String videoId, String originalFileName) {
		if (originalFileName == null || !originalFileName.contains(".")) {
			throw new BusinessRuleException(
					"INVALID FILE NAME FOR ZIP GENERATION: ORIGINAL FILE NAME IS NULL OR HAS NO EXTENSION");
		}

		String baseName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
		return String.format("%s/%s-%s-frames.zip", userId, videoId, baseName);
	}

}
