package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.ffmpeg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger LOGGER = LoggerFactory.getLogger(FFmpegFrameExtractor.class);

	public File extractFramesFromVideo(File videoFile) {
		 if (videoFile == null || !videoFile.exists()) {
		        throw new BusinessRuleException("Input video file is null or does not exist.");
		    }

		    LOGGER.info("#### FRAME EXTRACTION INITIATED FOR FILE: {} ####", videoFile.getAbsolutePath());

		    long start = System.currentTimeMillis();
		    File zipFile;

		    try {
		        zipFile = File.createTempFile("video-frames", ".zip");
		        extractFramesAndWriteToZip(videoFile, zipFile);
		    } catch (IOException e) {
		        throw new BusinessRuleException("Failed to create temp zip file.", e);
		    }

		    long duration = System.currentTimeMillis() - start;
		    LOGGER.info("#### ZIP CREATED IN {} ms: {} ####", duration, zipFile.getAbsolutePath());

		    return zipFile;
	}

	public File extractFrames(MultipartFile multipartFile) {
		File temporaryVideoFile = null;

	    try {
	        // 1. Converter MultipartFile em arquivo físico temporário
	        temporaryVideoFile = convertMultipartFileToFile(multipartFile);

	        // 2. Criar o arquivo ZIP temporário para armazenar os frames
	        File zipOutputFile = File.createTempFile("video-frames", ".zip");

	        // 3. Extrair os frames diretamente para o ZIP
	        extractFramesAndWriteToZip(temporaryVideoFile, zipOutputFile);

	        // 4. Retornar o arquivo ZIP com os frames
	        return zipOutputFile;

	    } catch (IOException e) {
	        throw new BusinessRuleException("Failed to process video file.", e);
	    } finally {
	        // 5. Limpar o vídeo temporário do disco
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

	public void extractFramesAndWriteToZip(File videoFile, File zipOutputFile) {
		FFmpegFrameGrabber frameGrabber = null;
		Java2DFrameConverter converter = null;

		long start = System.currentTimeMillis();

		try (FileOutputStream fos = new FileOutputStream(zipOutputFile);
				ZipOutputStream zipOut = new ZipOutputStream(fos)) {

			frameGrabber = new FFmpegFrameGrabber(videoFile);
			frameGrabber.start();
			converter = new Java2DFrameConverter();

			LOGGER.info("#### STREAMING FRAMES TO ZIP: {} ####", zipOutputFile.getAbsolutePath());

			Frame frame;
			int frameNumber = 0;
			long nextTargetTimestamp = 0;

			while ((frame = frameGrabber.grabImage()) != null) {
				long currentTimestamp = frameGrabber.getTimestamp() / 1000;

				if (currentTimestamp >= nextTargetTimestamp) {
					BufferedImage bufferedImage = converter.convert(frame);

					if (bufferedImage != null) {
						String fileName = FRAME_FILE_PREFIX + frameNumber + "." + FRAME_FILE_EXTENSION;
						ZipEntry entry = new ZipEntry(fileName);
						zipOut.putNextEntry(entry);

						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						ImageIO.write(bufferedImage, FRAME_FILE_EXTENSION, baos);
						zipOut.write(baos.toByteArray());
						zipOut.closeEntry();

						LOGGER.debug("#### FRAME {} ADDED TO ZIP", frameNumber);

						frameNumber++;
						nextTargetTimestamp = currentTimestamp + FRAME_INTERVAL_MS;
					}
				}
			}

			LOGGER.info("#### ZIP CREATED WITH {} FRAMES ####", frameNumber);

		} catch (Exception e) {
			throw new BusinessRuleException("Failed to extract and write frames to zip file.", e);
		} finally {
			if (frameGrabber != null) {
				try {
					frameGrabber.stop();
					frameGrabber.release();
					frameGrabber.close();
				} catch (Exception ignored) {
				}
			}

			if (converter != null) {
				try {
					converter.close();
				} catch (Exception ignored) {
				}
			}

			LOGGER.info("#### TOTAL STREAM-TO-ZIP TIME: {} ms ####", (System.currentTimeMillis() - start));
		}
	}

}
