package br.duosilva.tech.solutions.ez.frame.generator.ms.adapters.out.s3;

import java.io.File;

import org.springframework.stereotype.Component;

import br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config.AmazonProperties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class AmazonS3Adapter {

	private final S3Client s3Client;
	private final AmazonProperties properties;

	public AmazonS3Adapter(S3Client s3Client, AmazonProperties properties) {
		this.s3Client = s3Client;
		this.properties = properties;
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
