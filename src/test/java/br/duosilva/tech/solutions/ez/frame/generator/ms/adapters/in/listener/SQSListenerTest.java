package br.duosilva.tech.solutions.ez.frame.generator.ms.adapters.in.listener;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.duosilva.tech.solutions.ez.frame.generator.ms.application.dto.VideoDataResponseDto;
import br.duosilva.tech.solutions.ez.frame.generator.ms.application.usecases.FrameGeneratorUseCase;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config.AmazonProperties;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config.AmazonProperties.Sqs;

import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.*;

class SQSListenerTest {

    private SQSListener sqsListener;
    private ObjectMapper objectMapper;
    private SqsAsyncClient sqsAsyncClient;
    private AmazonProperties amazonProperties;
    private FrameGeneratorUseCase frameGeneratorUseCase;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = mock(ObjectMapper.class);
        sqsAsyncClient = mock(SqsAsyncClient.class);
        amazonProperties = mock(AmazonProperties.class);
        frameGeneratorUseCase = mock(FrameGeneratorUseCase.class);

        AmazonProperties.Sqs sqsConfig = mock(Sqs.class);
        when(amazonProperties.getSqs()).thenReturn(sqsConfig);
        when(sqsConfig.getQueueName()).thenReturn("my-queue");

        // Mock do retorno da URL da fila
        when(sqsAsyncClient.getQueueUrl(any(GetQueueUrlRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        GetQueueUrlResponse.builder().queueUrl("http://fake-queue-url").build()));

        // Mock do retorno do ObjectMapper
        when(objectMapper.readValue(anyString(), eq(VideoDataResponseDto.class)))
                .thenReturn(new VideoDataResponseDto(
                        "id", "videoId", "bucket", "objectKey", "resultKey", "COMPLETED", null));

        sqsListener = new SQSListener(objectMapper, sqsAsyncClient, amazonProperties, frameGeneratorUseCase);
    }

    @Test
    void shouldProcessMessageAndDeleteIt() {
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

        // Act
        sqsListener.pollMessagesFromQueue();

        // Assert
        verify(frameGeneratorUseCase, times(1)).retrieveAndProcessBucketVideo(any(VideoDataResponseDto.class));
        verify(sqsAsyncClient, times(1)).deleteMessage(any(DeleteMessageRequest.class));
    }


    @Test
    void shouldNotFailWhenNoMessages() {
        ReceiveMessageResponse response = ReceiveMessageResponse.builder().messages(List.of()).build();
        when(sqsAsyncClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        sqsListener.pollMessagesFromQueue();

        verify(frameGeneratorUseCase, never()).retrieveAndProcessBucketVideo(any());
    }
}
