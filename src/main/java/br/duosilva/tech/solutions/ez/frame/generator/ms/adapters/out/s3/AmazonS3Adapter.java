package br.duosilva.tech.solutions.ez.frame.generator.ms.adapters.out.s3;

import java.io.File;
import java.io.InputStream;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.duosilva.tech.solutions.ez.frame.generator.ms.application.usecases.FrameGeneratorUseCase;
import br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception.BusinessRuleException;
import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config.AmazonProperties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.core.ResponseInputStream;

@Component
public class AmazonS3Adapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AmazonS3Adapter.class);

	private final S3Client s3Client;
	private final AmazonProperties properties;
	private final S3Presigner s3Presigner;

	public AmazonS3Adapter(S3Client s3Client, AmazonProperties properties, S3Presigner s3Presigner) {
		this.s3Client = s3Client;
		this.properties = properties;
		this.s3Presigner = s3Presigner;
	}

	/**
	 * Uploads a .zip file to the configured S3 bucket.
	 *
	 * @param key     the object key (ex: userId/filename.zip)
	 * @param zipFile the zip file to upload
	 */
	public void uploadZipToS3(String key, File zipFile) {
		String bucketName = properties.getS3().getBucketName();

		PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName).key(key).contentType("application/zip")
				.build();

		s3Client.putObject(request, RequestBody.fromFile(zipFile.toPath()));
	}

	public String generatePresignedUrl(String objectKey, Duration expiration) {
		String bucketName = properties.getS3().getBucketName();

		GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(objectKey).build();

		GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder().getObjectRequest(getObjectRequest)
				.signatureDuration(expiration).build();

		PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

		return presignedRequest.url().toString();
	}

	public InputStream downloadVideo(String bucketName, String objectKey) {
		LOGGER.info("#### STARTING DOWNLOAD PROCESS: {}/{} ####", bucketName, objectKey);

		GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName).key(objectKey).build();

		try {
			ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(request);
			return inputStream;
		} catch (Exception e) {
			throw new BusinessRuleException("FAILED TO DOWNLOAD S3 OBJECT: " + bucketName + "/" + objectKey, e);
		}
	}

	/**
	 * Checks if the zip file already exists in the bucket.
	 *
	 * @param key object key
	 * @return true if object exists
	 */
	public boolean doesZipExistInS3(String key) {
		String bucketName = properties.getS3().getBucketName();
		return s3Client.listObjectsV2(builder -> builder.bucket(bucketName).prefix(key)).contents().stream()
				.anyMatch(obj -> obj.key().equals(key));
	}

}
