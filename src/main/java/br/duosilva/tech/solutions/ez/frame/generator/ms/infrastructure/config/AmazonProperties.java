package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws")
public class AmazonProperties {

	private String region;
	private Credentials credentials;
	private S3 s3;
	private Sqs sqs;
	private DynamoDb dynamodb;

	public static class Credentials {
		private String accessKey;
		private String secretKey;

		public String getAccessKey() {
			return accessKey;
		}

		public void setAccessKey(String accessKey) {
			this.accessKey = accessKey;
		}

		public String getSecretKey() {
			return secretKey;
		}

		public void setSecretKey(String secretKey) {
			this.secretKey = secretKey;
		}

	}

	public static class S3 {
		private String bucketName;
		private String endpoint;

		public String getBucketName() {
			return bucketName;
		}

		public void setBucketName(String bucketName) {
			this.bucketName = bucketName;
		}

		public String getEndpoint() {
			return endpoint;
		}

		public void setEndpoint(String endpoint) {
			this.endpoint = endpoint;
		}

	}

	public static class Sqs {
		private String queueName;
		private String resultQueueName;
		private String endpoint;

		public String getQueueName() {
			return queueName;
		}

		public void setQueueName(String queueName) {
			this.queueName = queueName;
		}

		public String getResultQueueName() {
			return resultQueueName;
		}

		public void setResultQueueName(String resultQueueName) {
			this.resultQueueName = resultQueueName;
		}

		public String getEndpoint() {
			return endpoint;
		}

		public void setEndpoint(String endpoint) {
			this.endpoint = endpoint;
		}
	}

	public static class DynamoDb {
		private String tableName;
		private String endpoint;
		
		public String getTableName() {
			return tableName;
		}
		public void setTableName(String tableName) {
			this.tableName = tableName;
		}
		public String getEndpoint() {
			return endpoint;
		}
		public void setEndpoint(String endpoint) {
			this.endpoint = endpoint;
		}
		
		
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public S3 getS3() {
		return s3;
	}

	public void setS3(S3 s3) {
		this.s3 = s3;
	}

	public Sqs getSqs() {
		return sqs;
	}

	public void setSqs(Sqs sqs) {
		this.sqs = sqs;
	}

	public DynamoDb getDynamodb() {
		return dynamodb;
	}

	public void setDynamodb(DynamoDb dynamodb) {
		this.dynamodb = dynamodb;
	}
	
}
