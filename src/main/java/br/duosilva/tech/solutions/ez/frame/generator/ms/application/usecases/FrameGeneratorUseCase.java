package br.duosilva.tech.solutions.ez.frame.generator.ms.application.usecases;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.duosilva.tech.solutions.ez.frame.generator.ms.adapters.out.s3.AmazonS3Adapter;
import br.duosilva.tech.solutions.ez.frame.generator.ms.application.dto.VideoDataResponseDto;
import br.duosilva.tech.solutions.ez.frame.generator.ms.domain.service.VideoProcessingService;
import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.BusinessRuleException;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.S3.S3KeyGenerator;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.utils.DateTimeUtils;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.utils.FileUtils;

@Component
public class FrameGeneratorUseCase {

	private static final Logger LOGGER = LoggerFactory.getLogger(FrameGeneratorUseCase.class);
	private static final Duration PRESIGNED_URL_DURATION = Duration.ofMinutes(15);

	private final VideoProcessingService videoProcessingService;
	private final AmazonS3Adapter amazonS3Adapter;
	private final VideoIngestionIntegrationUseCase videoIngestionIntegrationUseCase;

	public FrameGeneratorUseCase(VideoProcessingService videoProcessingService, AmazonS3Adapter amazonS3Adapter,
			VideoIngestionIntegrationUseCase videoIngestionIntegrationUseCase) {
		this.videoProcessingService = videoProcessingService;
		this.amazonS3Adapter = amazonS3Adapter;
		this.videoIngestionIntegrationUseCase = videoIngestionIntegrationUseCase;
	}

	public void initiateFrameGenerationProcess(VideoDataResponseDto dto) {
		File videoFile = null;
		File zipFile = null;
		long start = System.currentTimeMillis();

		LOGGER.info("#### FRAME GENERATION PROCESS STARTED: {} ####", dto.getOriginalFileName());

		try {
			videoFile = downloadVideoAsFile(dto);
			String s3Key = S3KeyGenerator.generateZipKey(dto.getUserId(), dto.getVideoId(), dto.getOriginalFileName());

			zipFile = generateZipFromVideo(videoFile);

			String presignedUrl = uploadZipAndGenerateUrl(s3Key, zipFile);

			notifyIngestionService(dto.getVideoId(), presignedUrl);

		} catch (Exception e) {
			throw new BusinessRuleException("FAILED TO PROCESS VIDEO", e);
		} finally {
			if (videoFile != null && videoFile.exists())
				videoFile.delete();
			if (zipFile != null && zipFile.exists())
				zipFile.delete();

			LOGGER.info("####  FRAME GENERATION PROCESS COMPLETED: {} ####", dto.getOriginalFileName());
			LOGGER.info("#### TOTAL TIME: {} ####", DateTimeUtils.formatDuration(System.currentTimeMillis() - start));
		}
	}

	private File downloadVideoAsFile(VideoDataResponseDto dto) throws IOException {
		InputStream stream = amazonS3Adapter.downloadVideo(dto.getS3BucketName(), dto.getS3Key());
		LOGGER.info("#### DOWNLOAD VIDEO PROCESS COMPLETED ####");
		return FileUtils.convertStreamToFile(stream, ".mp4");
	}

	private File generateZipFromVideo(File videoFile) {
		File zip = videoProcessingService.generateVideoFrames(videoFile);
		LOGGER.info("#### GENERATE ZIP COMPLETED ####");
		return zip;
	}

	private String uploadZipAndGenerateUrl(String s3Key, File zipFile) {
		if (amazonS3Adapter.doesZipExistInS3(s3Key)) {
			LOGGER.warn("#### ZIP ALREADY EXISTS IN S3: {} — SKIPPING UPLOAD ####", s3Key);
		} else {
			amazonS3Adapter.uploadZipToS3(s3Key, zipFile);
			LOGGER.info("#### ZIP UPLOADED TO S3: {} ####", s3Key);
		}
		String url = amazonS3Adapter.generatePresignedUrl(s3Key, PRESIGNED_URL_DURATION);
		LOGGER.info("#### PRESIGNED URL: {} ####", url);
		return url;
	}

	private void notifyIngestionService(String videoId, String presignedUrl) {
		videoIngestionIntegrationUseCase.updateVideoProcessingStatus(videoId, "COMPLETED", presignedUrl);
	}

}
