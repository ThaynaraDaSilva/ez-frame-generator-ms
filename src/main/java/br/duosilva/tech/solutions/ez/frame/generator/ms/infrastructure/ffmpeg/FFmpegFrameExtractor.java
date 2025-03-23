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
import org.springframework.web.multipart.MultipartFile;

import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.BusinessRuleException;

public class FFmpegFrameExtractor {
	
	
	public List<File> extractFrames(MultipartFile multipartFile) {
	    List<File> frames = new ArrayList<>();

	    try {
	        // Salvar temporariamente o vídeo no disco
	        File tempVideoFile = File.createTempFile("uploaded-", ".mp4");
	        multipartFile.transferTo(tempVideoFile);

	        // Criar pasta temporária para salvar os frames
	        File tempFrameDir = Files.createTempDirectory("frames").toFile();

	        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(tempVideoFile);
	        frameGrabber.start();

	        int frameNumber = 0;
	        Frame frame;
	        Java2DFrameConverter converter = new Java2DFrameConverter();

	        while ((frame = frameGrabber.grabImage()) != null) {
	            BufferedImage bufferedImage = converter.convert(frame);
	            if (bufferedImage != null) {
	                File frameFile = new File(tempFrameDir, "frame_" + frameNumber + ".jpg");
	                ImageIO.write(bufferedImage, "jpg", frameFile);
	                frames.add(frameFile);
	                frameNumber++;
	            }
	        }

	        frameGrabber.stop();
	        frameGrabber.release();

	    } catch (IOException e) {
	    	throw new BusinessRuleException("Error extracting frames from video");
	    }

	    return frames;
	}

}
