package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.InputStream;

import org.springframework.stereotype.Component;

import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.BusinessRuleException;

@Component
public class FileUtils {
	
	public static File convertStreamToFile(InputStream inputStream, String suffix) {
	    try {
	        File tempFile = File.createTempFile("tmp-",suffix);
	        Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	        return tempFile;
	    } catch (Exception e) {
	        throw new BusinessRuleException("FAILED TO CONVERT INPUT STREAM TO TEMP FILE", e);
	    }
	}


}
