package br.duosilva.tech.solutions.ez.frame.generator.ms.domain.service;

import java.io.File;
import java.time.Duration;
import java.util.List;


import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.duosilva.tech.solutions.ez.frame.generator.ms.adapters.out.s3.AmazonS3Adapter;
import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.BusinessRuleException;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.ffmpeg.FFmpegFrameExtractor;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.zip.ZipFileGenerator;

@Service
public class VideoProcessingService {

	private final FFmpegFrameExtractor ffmpegFrameExtractor;
	private final ZipFileGenerator zipFileGenerator;
	private final AmazonS3Adapter amazonS3Adapter;
	private static final Logger LOGGER = LoggerFactory.getLogger(VideoProcessingService.class);

	public VideoProcessingService(FFmpegFrameExtractor ffmpegFrameExtractor, ZipFileGenerator zipFileGenerator,AmazonS3Adapter amazonS3Adapter) {
		this.ffmpegFrameExtractor = ffmpegFrameExtractor;
		this.zipFileGenerator = zipFileGenerator;
		this.amazonS3Adapter = amazonS3Adapter;
	}

	// Avaliar a trocar para o tipo de retorno de File para VideoMetadata quando
	// implementar integração com Dynamo

	/**
	 * Processa um vídeo enviado pelo usuário, extraindo os frames e gerando um
	 * arquivo .zip com as imagens.
	 *
	 * @param multipartFile Arquivo de vídeo enviado pelo usuário
	 * @param userId        ID do usuário
	 * @return Arquivo .zip contendo os frames extraídos
	 */
	public File generateFrames(MultipartFile multipartFile, String userId) {
		 long startTime = System.currentTimeMillis();
		 LOGGER.info("############################################################");
		 LOGGER.info("#### VIDEO PROCESSING STARTED: {} ####", multipartFile.getOriginalFilename());

		try {
			// 1. Extrair frames e salvar temporariamente
			List<File> frames = ffmpegFrameExtractor.extractFrames(multipartFile);

			// 2.Gerar baseName do vídeo original
			String baseName = FilenameUtils.getBaseName(multipartFile.getOriginalFilename());

			// 3. Gerar o .zip dos frames
			File zipFile = compressFrames(frames, baseName);

			// 4. Limpar arquivos de frame individuais
			for (File frame : frames) {
				frame.delete();
			}

			// 5. Deletar diretorio temporario (caso esteja vazio)
			if (!frames.isEmpty()) {
	            File frameDirectory = frames.get(0).getParentFile();
	            frameDirectory.delete();
	        }
			// 6. Upload to S3
            String s3ObjectKey = userId + "/" + zipFile.getName();
            if (amazonS3Adapter.doesZipExistInS3(s3ObjectKey)) {
            	LOGGER.warn("#### ZIP ALREADY EXISTS IN S3: {} — SKIPPING UPLOAD ####", s3ObjectKey);
            } else {
                amazonS3Adapter.uploadZipToS3(s3ObjectKey, zipFile);
                LOGGER.info("#### ZIP UPLOADED TO S3: {} ####", s3ObjectKey);
            }
            
         // 7. Generate presigned URL (15 min) and log it
            String presignedUrl = amazonS3Adapter.generatePresignedUrl(s3ObjectKey, Duration.ofMinutes(15));
            LOGGER.info("#### PRESIGNED URL (VALID FOR 15 MINUTES): {} ####", presignedUrl);

			return zipFile;

		} catch (Exception e) {
			throw new BusinessRuleException("Failed to process video and generate frames: " + e.getMessage());
		}finally {
			 long endTime = System.currentTimeMillis();
		        long duration = endTime - startTime;

		        LOGGER.info("#### VIDEO PROCESSING COMPLETED: {} ####", multipartFile.getOriginalFilename());
		        LOGGER.info("#### TOTAL PROCESSING TIME: {} ####", formatDuration(duration));

		}
	}

	/**
	 * Gera um arquivo .zip contendo os frames extraídos do vídeo.
	 *
	 * @param frames   Lista de arquivos de imagem (frames)
	 * @param baseName Nome base para o arquivo .zip
	 * @return Arquivo .zip gerado
	 */
	private File compressFrames(List<File> frames, String baseName) {
		return zipFileGenerator.generateZipFromFrames(frames, baseName);
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
