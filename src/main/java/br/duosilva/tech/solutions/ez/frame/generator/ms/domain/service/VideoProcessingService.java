package br.duosilva.tech.solutions.ez.frame.generator.ms.domain.service;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.BusinessRuleException;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.ffmpeg.FFmpegFrameExtractor;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.zip.ZipFileGenerator;

@Service
public class VideoProcessingService {

	private final FFmpegFrameExtractor ffmpegFrameExtractor;
	private final ZipFileGenerator zipFileGenerator;

	public VideoProcessingService(FFmpegFrameExtractor ffmpegFrameExtractor, ZipFileGenerator zipFileGenerator) {
		this.ffmpegFrameExtractor = ffmpegFrameExtractor;
		this.zipFileGenerator = zipFileGenerator;
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
			File frameDirectory = frames.get(0).getParentFile();
			frameDirectory.delete(); // so funciona se a pasta estiver vazia

			return zipFile;

		} catch (Exception e) {
			throw new BusinessRuleException("Failed to process video and generate frames: " + e.getMessage());
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

}
