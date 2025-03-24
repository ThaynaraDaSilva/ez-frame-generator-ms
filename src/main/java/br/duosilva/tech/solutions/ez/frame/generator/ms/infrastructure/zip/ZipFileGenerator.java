package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.BusinessRuleException;

@Component
public class ZipFileGenerator {
	
	@Value("${video.output.zip-directory:/tmp}")
	private String zipOutputDirectory;

	private static final String ZIP_FILE_SUFFIX = "-frames.zip";
	//private static final String TEMP_ZIP_DIR_PREFIX = "zip-output";

	public File generateZipFromFrames(List<File> frames, String baseName) {
		try {
			//File tempDir = Files.createTempDirectory(TEMP_ZIP_DIR_PREFIX).toFile();
			File zipFile = new File(zipOutputDirectory, baseName + ZIP_FILE_SUFFIX);

			try (FileOutputStream fos = new FileOutputStream(zipFile); ZipOutputStream zos = new ZipOutputStream(fos)) {

				for (File frame : frames) {
					try (FileInputStream fis = new FileInputStream(frame)) {
						ZipEntry zipEntry = new ZipEntry(frame.getName());
						zos.putNextEntry(zipEntry);

						byte[] buffer = new byte[1024];
						int length;
						while ((length = fis.read(buffer)) > 0) {
							zos.write(buffer, 0, length);
						}

						zos.closeEntry();
					}
				}
			}

			return zipFile;

		} catch (IOException e) {
			throw new BusinessRuleException("Failed to generate zip file from frames: " + e.getMessage());
		}
	}
}
