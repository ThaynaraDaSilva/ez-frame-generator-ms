package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.zip;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipFile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.BusinessRuleException;

class ZipFileGeneratorTest {

    @TempDir
    File tempDir;

    private final ZipFileGenerator zipFileGenerator = new ZipFileGenerator();

    @Test
    void generateZipFromFrames_ShouldCreateZipWithAllFiles() throws IOException {
        // Arrange
        File frame1 = createTempTextFile("frame1.txt", "Hello", tempDir);
        File frame2 = createTempTextFile("frame2.txt", "World", tempDir);
        List<File> frames = List.of(frame1, frame2);

        // Act
        File zipFile = zipFileGenerator.generateZipFromFrames(frames, "testZip");

        // Assert
        assertNotNull(zipFile, "The zip file should not be null");
        assertTrue(zipFile.exists(), "The zip file should exist");
        assertTrue(zipFile.getName().endsWith("-frames.zip"), "The zip file should have the correct suffix");

        // Check contents of the zip file
        try (ZipFile zip = new ZipFile(zipFile)) {
            assertNotNull(zip.getEntry("frame1.txt"), "Zip should contain frame1.txt");
            assertNotNull(zip.getEntry("frame2.txt"), "Zip should contain frame2.txt");
        }

        // Clean up
        assertTrue(zipFile.delete(), "Zip file should be deleted after test");
    }

    @Test
    void generateZipFromFrames_ShouldThrowBusinessRuleException_WhenFileIsMissing() {
        // Arrange
        File nonExistentFile = new File(tempDir, "nonexistent.txt");

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            zipFileGenerator.generateZipFromFrames(List.of(nonExistentFile), "invalid");
        });

        assertTrue(exception.getMessage().contains("Failed to generate zip file"), "Error message should indicate failure");
    }

    private File createTempTextFile(String fileName, String content, File directory) throws IOException {
        File file = new File(directory, fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }
}
