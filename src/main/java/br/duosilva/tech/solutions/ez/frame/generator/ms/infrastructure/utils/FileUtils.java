package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.duosilva.tech.solutions.ez.frame.generator.ms.adapters.out.s3.AmazonS3Adapter;
import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.BusinessRuleException;

@Component
public class FileUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
	
	public static File convertStreamToFile(InputStream inputStream, String suffix) {
		long start = System.currentTimeMillis();
	    try {
	        File tempFile = File.createTempFile("tmp-",suffix);
	        Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	        long duration = System.currentTimeMillis() - start;
	        LOGGER.info("#### STREAM COPIED TO FILE IN {} ms ####", duration);
	        return tempFile;
	    } catch (Exception e) {
	        throw new BusinessRuleException("FAILED TO CONVERT INPUT STREAM TO TEMP FILE", e);
	    }
	}


}
