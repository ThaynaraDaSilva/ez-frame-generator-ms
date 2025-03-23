package br.duosilva.tech.solutions.ez.frame.generator.ms.application.usecases;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UploadVideoUseCase {

	
	public void processUploadedVideo(MultipartFile[] files) {
		System.out.println("################################");
		System.out.println("PROCESS UPLOADED VIDEO");
		System.out.println("################################");
	}
	
}
