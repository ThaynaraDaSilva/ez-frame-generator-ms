package br.duosilva.tech.solutions.ez.frame.generator.ms.adapters.in.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.duosilva.tech.solutions.ez.frame.generator.ms.application.dto.VideoDataResponseDTO;
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

	public SQSListener(ObjectMapper objectMapper, SqsAsyncClient sqsAsyncClient, AmazonProperties amazonProperties,
			FrameGeneratorUseCase frameGeneratorUseCase) {
		super();
		this.objectMapper = objectMapper;
		this.sqsAsyncClient = sqsAsyncClient;
		this.amazonProperties = amazonProperties;
		this.frameGeneratorUseCase = frameGeneratorUseCase;
	}

	private String obtainSQSUrl() {
		return sqsAsyncClient
				.getQueueUrl(GetQueueUrlRequest.builder().queueName(amazonProperties.getSqs().getQueueName()).build())
				.join().queueUrl(); // forca esperar o resultado

	}

	@Scheduled(fixedDelay = 120000)  // 5000 Runs every 5 seconds
	public void pollMessagesFromQueue() {
		// Fetch up to 5 messages at a time
		ReceiveMessageRequest request = ReceiveMessageRequest.builder().queueUrl(obtainSQSUrl()).maxNumberOfMessages(5) // time
				.build();

		ReceiveMessageResponse response = sqsAsyncClient.receiveMessage(request).join();

		if (response.messages().isEmpty()) {
			LOGGER.info("############################################################");
			LOGGER.info("#### NO MESSAGES: {} ####");
		} else {
			for (Message message : response.messages()) {
				retrieveVideoData(message.body(), message.receiptHandle());
			}
		}
	}

	private void retrieveVideoData(String message, String receiptHandle) {
		try {

			VideoDataResponseDTO videoDataResponse = objectMapper.readValue(message, VideoDataResponseDTO.class);

			// retrieve video
			frameGeneratorUseCase.retrieveAndProcessBucketVideo(videoDataResponse);
			LOGGER.info("############################################################");
			LOGGER.info("RETRIEVE VIDEO FROM BUCKET COMPLETED");

			// Delete the message from the queue
			deleteMessage(receiptHandle);

		} catch (Exception e) {
			LOGGER.error("############################################################: {} ", e);
		}
	}

	private void deleteMessage(String receiptHandle) {
		DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder().queueUrl(obtainSQSUrl())
				.receiptHandle(receiptHandle).build();

		sqsAsyncClient.deleteMessage(deleteMessageRequest);
	}

}
