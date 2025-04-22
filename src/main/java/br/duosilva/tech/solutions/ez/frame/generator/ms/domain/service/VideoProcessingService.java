package br.duosilva.tech.solutions.ez.frame.generator.ms.domain.service;

import java.io.File;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.ffmpeg.FFmpegFrameExtractor;

@Service
public class VideoProcessingService {

	private final FFmpegFrameExtractor ffmpegFrameExtractor;

	public VideoProcessingService(FFmpegFrameExtractor ffmpegFrameExtractor) {
		this.ffmpegFrameExtractor = ffmpegFrameExtractor;
	}
	public File generateVideoFrames(File videoFile) {
		return ffmpegFrameExtractor.extractFramesFromVideo(videoFile);
	}

	public File generateFrames(MultipartFile multipartFile) {
		return ffmpegFrameExtractor.extractFrames(multipartFile);
	}

}
