package br.duosilva.tech.solutions.ez.frame.generator.ms.domain.service;


import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.BusinessRuleException;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.ffmpeg.FFmpegFrameExtractor;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.storage.ZipFileGenerator;

@Service
public class VideoProcessingService {
	
	
	//Trocar para o tipo de retorno de File para VideoMetadata 

	public File generateFrames(MultipartFile multipartFile, String userId) {
		
		System.out.println("\n ################################ GENERATE FRAMES");
		
		try {
	        // Extrair frames e salvar temporariamente
			
			FFmpegFrameExtractor extractor = new FFmpegFrameExtractor();
	        List<File> frames = extractor.extractFrames(multipartFile);

	        // Gerar baseName do vídeo original
	        String baseName = FilenameUtils.getBaseName(multipartFile.getOriginalFilename());

	        // Gerar o .zip dos frames
	        ZipFileGenerator zipFileGenerator = new ZipFileGenerator();
	        File zipFile = zipFileGenerator.generateZipFromFrames(frames, baseName);

	        return zipFile;

	    } catch (Exception e) {
	        throw new BusinessRuleException("Failed to process video and generate frames: " + e.getMessage());
	    }
	}
	
	
	

	
	

}
