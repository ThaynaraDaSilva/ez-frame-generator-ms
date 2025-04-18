package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.ffmpeg;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.BusinessRuleException;

@Component
public class FFmpegFrameExtractor {

	private static final String TEMP_VIDEO_PREFIX = "uploaded-video";
	private static final String TEMP_VIDEO_DEFAULT_EXTENSION = ".mp4";
	private static final String TEMP_FRAME_DIR_PREFIX = "frames-generator-ms";
	private static final String FRAME_FILE_PREFIX = "frame-";
	private static final String FRAME_FILE_EXTENSION = "jpg";
	private static final long FRAME_INTERVAL_MS = 1000; // REMOVER POSTERIORMENTE

	
	public List<File> extractFramesFromVideo(File videoFile) {
	    // 1. Criar diretório temporário para os frames
	    File frameOutputDirectory = createTemporaryFrameDirectory();

	    // 2. Extrair os frames diretamente do arquivo físico
	    return extractFramesFromVideoFile(videoFile, frameOutputDirectory);
	}

	
	public List<File> extractFrames(MultipartFile multipartFile) {
		File temporaryVideoFile = null;

		try {
			// 1. Converter MultipartFile em arquivo físico temporario
			temporaryVideoFile = convertMultipartFileToFile(multipartFile);

			// 2. Criar diretorio temporario para os frames
			File frameOutputDirectory = createTemporaryFrameDirectory();

			// 3. Extrair os frames do video
			return extractFramesFromVideoFile(temporaryVideoFile, frameOutputDirectory);

		} finally {
			// 4. Limpar o video temporario do disco
			if (temporaryVideoFile != null && temporaryVideoFile.exists()) {
				temporaryVideoFile.delete();
			}
		}
	}


	private File convertMultipartFileToFile(MultipartFile multipartFile) {
		try {
			String suffix = this.getFileExtension(multipartFile.getOriginalFilename());
			File tempVideoFile = File.createTempFile(TEMP_VIDEO_PREFIX, suffix);
			multipartFile.transferTo(tempVideoFile);
			return tempVideoFile;
		} catch (IOException e) {
			throw new BusinessRuleException("Failed to convert uploaded video to a temporary file.");
		}
	}

	private String getFileExtension(String fileName) {
		if (fileName != null && fileName.contains(".")) {
			return fileName.substring(fileName.lastIndexOf("."));
		}
		return TEMP_VIDEO_DEFAULT_EXTENSION;
	}

	private File createTemporaryFrameDirectory() {
		try {
			return Files.createTempDirectory(TEMP_FRAME_DIR_PREFIX).toFile();
		} catch (IOException e) {
			throw new BusinessRuleException("Failed to create temporary directory for extracted frames.");
		}
	}

	private List<File> extractFramesFromVideoFile(File videoFile, File outputDirectory) {
		List<File> extractedFrames = new ArrayList<>();
		FFmpegFrameGrabber frameGrabber = null;
		Java2DFrameConverter converter = null;

		try {
			frameGrabber = new FFmpegFrameGrabber(videoFile);
			frameGrabber.start();

			Frame frame;
			int frameNumber = 0;
			converter = new Java2DFrameConverter();

			long nextTargetTimestamp = 0;

			while ((frame = frameGrabber.grabImage()) != null) {

				long currentTimestamp = frameGrabber.getTimestamp() / 1000;
				if (currentTimestamp >= nextTargetTimestamp) {
					BufferedImage bufferedImage = converter.convert(frame);
					if (bufferedImage != null) {
						File frameFile = new File(outputDirectory,
								FRAME_FILE_PREFIX + frameNumber + "." + FRAME_FILE_EXTENSION);
						ImageIO.write(bufferedImage, FRAME_FILE_EXTENSION, frameFile);
						extractedFrames.add(frameFile);
						frameNumber++;
						nextTargetTimestamp = currentTimestamp + FRAME_INTERVAL_MS;
					}
				}
			}
		} catch (Exception e) {
			throw new BusinessRuleException("Failed to extract frames from video file.");
		} finally {
			// Libera frameGrabber
			if (frameGrabber != null) {
				try {
					frameGrabber.stop();
					frameGrabber.release();
					frameGrabber.close();
				} catch (Exception ignored) {
				}
			}

			// Libera converter
			if (converter != null) {
				try {
					converter.close();
				} catch (Exception ignored) {
				}
			}
		}
		return extractedFrames;
	}

}
