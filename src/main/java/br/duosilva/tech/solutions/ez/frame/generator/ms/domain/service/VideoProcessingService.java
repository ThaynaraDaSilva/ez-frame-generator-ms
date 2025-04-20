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
	private static final Logger LOGGER = LoggerFactory.getLogger(VideoProcessingService.class);

	public VideoProcessingService(FFmpegFrameExtractor ffmpegFrameExtractor) {
		this.ffmpegFrameExtractor = ffmpegFrameExtractor;
	}
	public File generateVideoFrames(File videoFile) {
		LOGGER.info("#### GENERATE VIDEO FRAMES FROM FILE ####");
		return ffmpegFrameExtractor.extractFramesFromVideo(videoFile);
	}

	public File generateFrames(MultipartFile multipartFile) {
		LOGGER.info("#### GENERATE VIDEO FRAMES FROM MULTIPART ####");
		return ffmpegFrameExtractor.extractFrames(multipartFile);
	}

}
