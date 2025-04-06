package br.duosilva.tech.solutions.ez.frame.generator.ms.application.usecases;

import java.io.File;
import java.io.InputStream;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.duosilva.tech.solutions.ez.frame.generator.ms.adapters.out.s3.AmazonS3Adapter;
import br.duosilva.tech.solutions.ez.frame.generator.ms.application.dto.VideoDataResponseDTO;
import br.duosilva.tech.solutions.ez.frame.generator.ms.domain.service.VideoProcessingService;
import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.BusinessRuleException;
import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.ErrorMessages;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.S3.S3KeyGenerator;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.utils.DateTimeUtils;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.utils.FileUtils;

@Component
public class FrameGeneratorUseCase {

	private static final Logger LOGGER = LoggerFactory.getLogger(FrameGeneratorUseCase.class);
	private static final Duration PRESIGNED_URL_DURATION = Duration.ofMinutes(15);

	private final VideoProcessingService videoProcessingService;
	private final AmazonS3Adapter amazonS3Adapter;

	public FrameGeneratorUseCase(VideoProcessingService videoProcessingService, AmazonS3Adapter amazonS3Adapter) {
		this.videoProcessingService = videoProcessingService;
		this.amazonS3Adapter = amazonS3Adapter;
	}

	public void retrieveAndProcessBucketVideo(VideoDataResponseDTO videoDataResponseDTO) {
		long startTime = System.currentTimeMillis();
		File videoFile = null;

		LOGGER.info("#### VIDEO PROCESSING STARTED: {} ####", videoDataResponseDTO.getOriginalFileName());

		try {
			// 1. Baixar video do S3 como InputStream e converter

			InputStream videoStream = amazonS3Adapter.downloadVideo(videoDataResponseDTO.getS3BucketName(),
					videoDataResponseDTO.getS3Key());

			videoFile = FileUtils.convertStreamToFile(videoStream, ".mp4");
			String s3ObjectKey = S3KeyGenerator.generateZipKey(videoDataResponseDTO.getUserId(),
					videoDataResponseDTO.getVideoId(), videoDataResponseDTO.getOriginalFileName());

			// 2. Gerar frames e criar ZIP
			File zipFile = videoProcessingService.generateVideoFrames(videoFile);

			// 3. Fazer upload do ZIP (se não existir)
			if (amazonS3Adapter.doesZipExistInS3(s3ObjectKey)) {
				LOGGER.warn("#### ZIP ALREADY EXISTS IN S3: {} — SKIPPING UPLOAD ####", s3ObjectKey);
			} else {
				amazonS3Adapter.uploadZipToS3(s3ObjectKey, zipFile);
				LOGGER.info("#### ZIP UPLOADED TO S3: {} ####", s3ObjectKey);
			}

			// 4. Gerar URL temporaria para download
			String presignedUrl = amazonS3Adapter.generatePresignedUrl(s3ObjectKey, PRESIGNED_URL_DURATION);
			LOGGER.info("#### PRESIGNED URL (VALID FOR 15 MINUTES): {} ####", presignedUrl);

		} catch (Exception e) {
			throw new BusinessRuleException("FAILED TO PROCESS VIDEO", e);
		} finally {

			if (videoFile != null && videoFile.exists()) {
				videoFile.delete();
			}

			long duration = System.currentTimeMillis() - startTime;
			LOGGER.info("#### VIDEO PROCESSING COMPLETED: {} ####", videoDataResponseDTO.getOriginalFileName());
			LOGGER.info("#### TOTAL PROCESSING TIME: {} ####", DateTimeUtils.formatDuration(duration));
		}
	}

}
