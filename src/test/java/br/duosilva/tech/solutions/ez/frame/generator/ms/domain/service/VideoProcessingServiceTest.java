/*
 * package br.duosilva.tech.solutions.ez.frame.generator.ms.domain.service;
 * 
 * import
 * br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.ffmpeg.
 * FFmpegFrameExtractor; import
 * br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.zip.
 * ZipFileGenerator; import org.junit.jupiter.api.BeforeEach; import
 * org.junit.jupiter.api.Test; import org.mockito.InjectMocks; import
 * org.mockito.Mock; import org.mockito.MockitoAnnotations; import
 * org.springframework.web.multipart.MultipartFile;
 * 
 * import java.io.File; import java.util.Arrays; import java.util.List;
 * 
 * import static org.junit.jupiter.api.Assertions.*; import static
 * org.mockito.Mockito.*;
 * 
 * class VideoProcessingServiceTest {
 * 
 * @Mock private FFmpegFrameExtractor ffmpegFrameExtractor;
 * 
 * @Mock private ZipFileGenerator zipFileGenerator;
 * 
 * @InjectMocks private VideoProcessingService videoProcessingService;
 * 
 * @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }
 * 
 * @Test void testGenerateVideoFrames() throws Exception { // Arrange File
 * videoFile = new File("test-video.mp4");
 * 
 * // Create mock frame files and their parent directory File frame1 =
 * mock(File.class); File frame2 = mock(File.class); File parentDirectory =
 * mock(File.class);
 * 
 * // Set up the behavior of the mock files
 * when(frame1.getParentFile()).thenReturn(parentDirectory);
 * when(frame2.getParentFile()).thenReturn(parentDirectory);
 * 
 * List<File> frames = Arrays.asList(frame1, frame2);
 * when(ffmpegFrameExtractor.extractFramesFromVideo(videoFile)).thenReturn(
 * frames);
 * 
 * File zipFile = new File("frames.zip");
 * when(zipFileGenerator.generateZipFromFrames(frames,
 * "test-video")).thenReturn(zipFile);
 * 
 * // Act File result = videoProcessingService.generateVideoFrames(videoFile);
 * 
 * // Assert assertEquals(zipFile, result);
 * verify(ffmpegFrameExtractor).extractFramesFromVideo(videoFile);
 * verify(zipFileGenerator).generateZipFromFrames(frames, "test-video");
 * verify(frame1).delete(); verify(frame2).delete();
 * verify(parentDirectory).delete(); // Verify that the parent directory is
 * deleted }
 * 
 * @Test void testGenerateFrames() throws Exception { // Arrange MultipartFile
 * multipartFile = mock(MultipartFile.class);
 * when(multipartFile.getOriginalFilename()).thenReturn("test-video.mp4");
 * 
 * // Create mock frame files and their parent directory File frame1 =
 * mock(File.class); File frame2 = mock(File.class); File parentDirectory =
 * mock(File.class);
 * 
 * // Set up the behavior of the mock files
 * when(frame1.getParentFile()).thenReturn(parentDirectory);
 * when(frame2.getParentFile()).thenReturn(parentDirectory);
 * 
 * List<File> frames = Arrays.asList(frame1, frame2);
 * when(ffmpegFrameExtractor.extractFrames(multipartFile)).thenReturn(frames);
 * 
 * File zipFile = new File("frames.zip");
 * when(zipFileGenerator.generateZipFromFrames(frames,
 * "test-video")).thenReturn(zipFile);
 * 
 * // Act File result = videoProcessingService.generateFrames(multipartFile);
 * 
 * // Assert assertEquals(zipFile, result);
 * verify(ffmpegFrameExtractor).extractFrames(multipartFile);
 * verify(zipFileGenerator).generateZipFromFrames(frames, "test-video");
 * verify(frame1).delete(); verify(frame2).delete();
 * verify(parentDirectory).delete(); // Verify that the parent directory is
 * deleted } }
 */