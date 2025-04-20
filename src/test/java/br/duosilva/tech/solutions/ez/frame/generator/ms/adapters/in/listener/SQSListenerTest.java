package br.duosilva.tech.solutions.ez.frame.generator.ms.adapters.in.listener;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.duosilva.tech.solutions.ez.frame.generator.ms.application.dto.VideoDataResponseDto;
import br.duosilva.tech.solutions.ez.frame.generator.ms.application.usecases.FrameGeneratorUseCase;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config.AmazonProperties;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config.AmazonProperties.Sqs;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

class SQSListenerTest {

    private SQSListener sqsListener;
    private ObjectMapper objectMapper;
    private SqsAsyncClient sqsAsyncClient;
    private AmazonProperties amazonProperties;
    private FrameGeneratorUseCase frameGeneratorUseCase;
    private ExecutorService executorService;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = mock(ObjectMapper.class);
        sqsAsyncClient = mock(SqsAsyncClient.class);
        amazonProperties = mock(AmazonProperties.class);
        frameGeneratorUseCase = mock(FrameGeneratorUseCase.class);
        executorService = Executors.newSingleThreadExecutor();

        AmazonProperties.Sqs sqsConfig = mock(AmazonProperties.Sqs.class);
        when(amazonProperties.getSqs()).thenReturn(sqsConfig);
        when(sqsConfig.getQueueName()).thenReturn("my-queue");

        when(sqsAsyncClient.getQueueUrl(any(GetQueueUrlRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        GetQueueUrlResponse.builder().queueUrl("http://fake-queue-url").build()));

        when(objectMapper.readValue(anyString(), eq(VideoDataResponseDto.class)))
                .thenReturn(new VideoDataResponseDto(
                        "id", "videoId", "bucket", "objectKey", "resultKey", "COMPLETED", null));

        sqsListener = new SQSListener(objectMapper, sqsAsyncClient, amazonProperties, frameGeneratorUseCase, executorService);
    }

    @Test
    void shouldProcessMessageAndDeleteIt() throws InterruptedException {
        // Arrange
        String messageBody = "{\"videoId\": \"videoId\"}";
        String receiptHandle = "abc123";

        Message message = Message.builder()
                .body(messageBody)
                .receiptHandle(receiptHandle)
                .build();

        ReceiveMessageResponse receiveResponse = ReceiveMessageResponse.builder()
                .messages(List.of(message))
                .build();

        when(sqsAsyncClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(receiveResponse));

        when(sqsAsyncClient.deleteMessage(any(DeleteMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(DeleteMessageResponse.builder().build()));

        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(frameGeneratorUseCase).initiateFrameGenerationProcess(any(VideoDataResponseDto.class));

        // Act
        sqsListener.pollMessagesFromQueue();

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertTrue(completed, "O processamento assíncrono não foi concluído a tempo.");

        // Assert
        verify(frameGeneratorUseCase, times(1)).initiateFrameGenerationProcess(any(VideoDataResponseDto.class));
        verify(sqsAsyncClient, times(1)).deleteMessage(any(DeleteMessageRequest.class));
    }

    @Test
    void shouldNotFailWhenNoMessages() {
        ReceiveMessageResponse response = ReceiveMessageResponse.builder().messages(List.of()).build();
        when(sqsAsyncClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        sqsListener.pollMessagesFromQueue();

        verify(frameGeneratorUseCase, never()).initiateFrameGenerationProcess(any());
    }
}
