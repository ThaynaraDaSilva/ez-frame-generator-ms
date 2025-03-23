package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileGenerator {

	public File generateZipFromFrames(List<File> frames, String baseName) {
		File zipFile = new File(baseName + "_frames.zip");
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
		} catch (IOException e) {
			throw new RuntimeException("Failed to generate zip file from frames", e);
		}

		return zipFile;
	}

}
