package br.duosilva.tech.solutions.ez.frame.generator.ms.domain.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.ffmpeg.FFmpegFrameExtractor;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.zip.ZipFileGenerator;

@Service
public class VideoProcessingService {

	private final FFmpegFrameExtractor ffmpegFrameExtractor;
	private final ZipFileGenerator zipFileGenerator;
	private static final Logger LOGGER = LoggerFactory.getLogger(VideoProcessingService.class);

	public VideoProcessingService(FFmpegFrameExtractor ffmpegFrameExtractor, ZipFileGenerator zipFileGenerator) {
		this.ffmpegFrameExtractor = ffmpegFrameExtractor;
		this.zipFileGenerator = zipFileGenerator;
	}

	public File generateVideoFrames(File videoFile) {

		List<File> frames = ffmpegFrameExtractor.extractFramesFromVideo(videoFile);
		String baseName = FilenameUtils.getBaseName(videoFile.getName());

		File zipFile = compressFrames(frames, baseName);

		LOGGER.info("#### COMPRESSION PROCESS FINISHED ####");
		
		frames.forEach(File::delete);

		if (!frames.isEmpty()) {
			LOGGER.info("#### GENERATE ZIP IF CONDITION ####");
			File frameDirectory = frames.get(0).getParentFile();
			frameDirectory.delete();
		}

		return zipFile;
	}

	// Avaliar a trocar para o tipo de retorno de File para VideoMetadata quando
	// implementar integraçao com Dynamo

	/**
	 * Processa um vídeo enviado pelo usuário, extraindo os frames e gerando um
	 * arquivo .zip com as imagens.
	 *
	 * @param multipartFile Arquivo de vídeo enviado pelo usuário
	 * @param userId        ID do usuário
	 * @return Arquivo .zip contendo os frames extraídos
	 */
	public File generateFrames(MultipartFile multipartFile) {
		List<File> frames = ffmpegFrameExtractor.extractFrames(multipartFile);
		String baseName = FilenameUtils.getBaseName(multipartFile.getOriginalFilename());
		File zipFile = compressFrames(frames, baseName);

		// Clean up
		frames.forEach(File::delete);
		if (!frames.isEmpty()) {
			File frameDirectory = frames.get(0).getParentFile();
			frameDirectory.delete();
		}

		return zipFile;
	}

	/**
	 * Gera um arquivo .zip contendo os frames extraídos do vídeo.
	 *
	 * @param frames   Lista de arquivos de imagem (frames)
	 * @param baseName Nome base para o arquivo .zip
	 * @return Arquivo .zip gerado
	 */
	private File compressFrames(List<File> frames, String baseName) {
		LOGGER.info("#### COMPRESS FRAMES METHOD ####");
		return zipFileGenerator.generateZipFromFrames(frames, baseName);
		
	}

}
