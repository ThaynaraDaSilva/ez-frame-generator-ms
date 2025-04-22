package br.duosilva.tech.solutions.ez.frame.generator.ms.adapters.in.listener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.duosilva.tech.solutions.ez.frame.generator.ms.application.dto.VideoDataResponseDto;
import br.duosilva.tech.solutions.ez.frame.generator.ms.application.usecases.FrameGeneratorUseCase;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config.AmazonProperties;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

@Component
public class SQSListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(SQSListener.class);

	private final ObjectMapper objectMapper;
	private final SqsAsyncClient sqsAsyncClient;
	private final AmazonProperties amazonProperties;
	private final FrameGeneratorUseCase frameGeneratorUseCase;
	private final ExecutorService executorService; 

	public SQSListener(ObjectMapper objectMapper, SqsAsyncClient sqsAsyncClient, AmazonProperties amazonProperties,
			FrameGeneratorUseCase frameGeneratorUseCase,ExecutorService executorService) {
		super();
		this.objectMapper = objectMapper;
		this.sqsAsyncClient = sqsAsyncClient;
		this.amazonProperties = amazonProperties;
		this.frameGeneratorUseCase = frameGeneratorUseCase;
		this.executorService = executorService;
	}

	private String obtainSQSUrl() {
		return sqsAsyncClient
				.getQueueUrl(GetQueueUrlRequest.builder().queueName(amazonProperties.getSqs().getQueueName()).build())
				.join().queueUrl(); // forca esperar o resultado

	}

	@Scheduled(fixedRate = 500) // 5000 Runs every 5 seconds
	public void pollMessagesFromQueue() {
		// Fetch up to 5 messages at a time
		ReceiveMessageRequest request = ReceiveMessageRequest.builder().queueUrl(obtainSQSUrl()).maxNumberOfMessages(10) // time
				.build();

		ReceiveMessageResponse response = sqsAsyncClient.receiveMessage(request).join();

		if (response.messages().isEmpty()) {
			LOGGER.info("#### NO MESSAGES ####");
		} else {
			for (Message message : response.messages()) {
				CompletableFuture.runAsync(() -> initiateGenerateFramesProcess(message.body(), message.receiptHandle()), executorService);
			}
		}
	}

	private void initiateGenerateFramesProcess(String message, String receiptHandle) {
		try {

			VideoDataResponseDto videoDataResponse = objectMapper.readValue(message, VideoDataResponseDto.class);

			// retrieve video
			frameGeneratorUseCase.initiateFrameGenerationProcess(videoDataResponse);
	
			LOGGER.info("#### RETRIEVE VIDEO FROM BUCKET COMPLETED ####");

			// Delete the message from the queue
			deleteMessage(receiptHandle);

		} catch (Exception e) {
			LOGGER.error("Error processing video message. Message body: {}. Error: {}", message, e.getMessage(), e);
		}
	}

	private void deleteMessage(String receiptHandle) {
		DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder().queueUrl(obtainSQSUrl())
				.receiptHandle(receiptHandle).build();

		sqsAsyncClient.deleteMessage(deleteMessageRequest).join();
		LOGGER.info("#### MESSAGE DELETED ####");
		LOGGER.info("############################################################");
	}

}
