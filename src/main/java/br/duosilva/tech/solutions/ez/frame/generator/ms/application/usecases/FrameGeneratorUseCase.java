package br.duosilva.tech.solutions.ez.frame.generator.ms.application.usecases;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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

/**
 * Use case responsável por orquestrar o processo de upload e processamento de
 * vídeos enviados pelo usuário. Aplica regras de negócio e aciona os serviços
 * responsáveis pelo processamento técnico.
 */

@Component
public class FrameGeneratorUseCase {

	private static final Logger LOGGER = LoggerFactory.getLogger(FrameGeneratorUseCase.class);

	private final VideoProcessingService videoProcessingService;
	private final AmazonS3Adapter amazonS3Adapter;

	public FrameGeneratorUseCase(VideoProcessingService videoProcessingService, AmazonS3Adapter amazonS3Adapter) {
		this.videoProcessingService = videoProcessingService;
		this.amazonS3Adapter = amazonS3Adapter;
	}

	public void retrieveAndProcessBucketVideo(VideoDataResponseDTO videoDataResponseDTO) {
	    long startTime = System.currentTimeMillis();
	    String originalFileName = videoDataResponseDTO.getOriginalFileName();
	    String userId = videoDataResponseDTO.getUserId();
	    String bucket = videoDataResponseDTO.getS3Bucket();
	    String key = videoDataResponseDTO.getS3Key();

	    LOGGER.info("############################################################");
	    LOGGER.info("#### VIDEO PROCESSING STARTED: {} ####", originalFileName);

	    File tempVideoFile = null;

	    try {
	        // 1. Baixar vídeo do S3 como InputStream
	        InputStream videoStream = amazonS3Adapter.downloadVideo(bucket, key);

	        // 2. Salvar vídeo como arquivo temporário
	        tempVideoFile = File.createTempFile("video-", ".mp4");
	        Files.copy(videoStream, tempVideoFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

	        // 3. Gerar frames e criar ZIP
	        File zipFile = videoProcessingService.generateVideoFrames(tempVideoFile);
	        String s3ObjectKey = userId + "/" + zipFile.getName();

	        // 4. Fazer upload do ZIP (se não existir)
	        if (amazonS3Adapter.doesZipExistInS3(s3ObjectKey)) {
	            LOGGER.warn("#### ZIP ALREADY EXISTS IN S3: {} — SKIPPING UPLOAD ####", s3ObjectKey);
	        } else {
	            amazonS3Adapter.uploadZipToS3(s3ObjectKey, zipFile);
	            LOGGER.info("#### ZIP UPLOADED TO S3: {} ####", s3ObjectKey);
	        }

	        // 5. Gerar URL temporária para download
	        String presignedUrl = amazonS3Adapter.generatePresignedUrl(s3ObjectKey, Duration.ofMinutes(15));
	        LOGGER.info("#### PRESIGNED URL (VALID FOR 15 MINUTES): {} ####", presignedUrl);

	    } catch (Exception e) {
	        throw new BusinessRuleException("FAILED TO PROCESS VIDEO:  " + e);
	    } finally {
	        if (tempVideoFile != null && tempVideoFile.exists()) {
	            tempVideoFile.delete();
	        }

	        long duration = System.currentTimeMillis() - startTime;
	        LOGGER.info("#### VIDEO PROCESSING COMPLETED: {} ####", originalFileName);
	        LOGGER.info("#### TOTAL PROCESSING TIME: {} ####", formatDuration(duration));
	    }
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
		long startTime = System.currentTimeMillis();
		LOGGER.info("############################################################");
		LOGGER.info("#### VIDEO PROCESSING STARTED: {} ####", file.getOriginalFilename());

		try {
			File zipFile = videoProcessingService.generateFrames(file);
			String s3ObjectKey = userId + "/" + zipFile.getName();

			if (amazonS3Adapter.doesZipExistInS3(s3ObjectKey)) {
				LOGGER.warn("#### ZIP ALREADY EXISTS IN S3: {} — SKIPPING UPLOAD ####", s3ObjectKey);
			} else {
				amazonS3Adapter.uploadZipToS3(s3ObjectKey, zipFile);
				LOGGER.info("#### ZIP UPLOADED TO S3: {} ####", s3ObjectKey);
			}

			String presignedUrl = amazonS3Adapter.generatePresignedUrl(s3ObjectKey, Duration.ofMinutes(15));
			LOGGER.info("#### PRESIGNED URL (VALID FOR 15 MINUTES): {} ####", presignedUrl);

		} catch (Exception e) {
			throw new BusinessRuleException("Failed to process video: " + e.getMessage());
		} finally {
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			LOGGER.info("#### VIDEO PROCESSING COMPLETED: {} ####", file.getOriginalFilename());
			LOGGER.info("#### TOTAL PROCESSING TIME: {} ####", formatDuration(duration));
		}
	}

	private String formatDuration(long millis) {
		Duration duration = Duration.ofMillis(millis);
		long hours = duration.toHours();
		long minutes = duration.toMinutesPart();
		long seconds = duration.toSecondsPart();
		long milliseconds = duration.toMillisPart();

		return String.format("%02dh %02dm %02ds %03dms", hours, minutes, seconds, milliseconds);
	}

}
