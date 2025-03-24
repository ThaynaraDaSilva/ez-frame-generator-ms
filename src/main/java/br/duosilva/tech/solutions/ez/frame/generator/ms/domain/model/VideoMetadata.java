package br.duosilva.tech.solutions.ez.frame.generator.ms.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class VideoMetadata {
	
	private final String id;
    private final String originalFileName;
    private final String contentType;
    private final long fileSizeBytes;

    private final LocalDateTime uploadedAt;

    private final String userId;
    private final String userEmail;

    private ProcessingStatus status;
    private String errorMessage;

    private String resultBucketName;
    private String resultObjectKey;
    private LocalDateTime processedAt;
	

	public VideoMetadata(String id, String originalFileName, String contentType, long fileSizeBytes,
			LocalDateTime uploadedAt, String userId, String userEmail, ProcessingStatus status, String errorMessage,
			String resultBucketName, String resultObjectKey, LocalDateTime processedAt) {
		super();
		this.id = UUID.randomUUID().toString();
		this.originalFileName = originalFileName;
		this.contentType = contentType;
		this.fileSizeBytes = fileSizeBytes;
		this.uploadedAt = uploadedAt;
		this.userId = userId;
		this.userEmail = userEmail;
		this.status = status;
		this.errorMessage = errorMessage;
		this.resultBucketName = resultBucketName;
		this.resultObjectKey = resultObjectKey;
		this.processedAt = processedAt;
	}

	public void markAsProcessing() {
        this.status = ProcessingStatus.PROCESSING;
    }

    public void markAsCompleted(String resultBucketName, String resultObjectKey) {
        this.status = ProcessingStatus.COMPLETED;
        this.resultBucketName = resultBucketName;
        this.resultObjectKey = resultObjectKey;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsFailed(String errorMessage) {
        this.status = ProcessingStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = LocalDateTime.now();
    }

	public ProcessingStatus getStatus() {
		return status;
	}

	public void setStatus(ProcessingStatus status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getResultBucketName() {
		return resultBucketName;
	}

	public void setResultBucketName(String resultBucketName) {
		this.resultBucketName = resultBucketName;
	}

	public String getResultObjectKey() {
		return resultObjectKey;
	}

	public void setResultObjectKey(String resultObjectKey) {
		this.resultObjectKey = resultObjectKey;
	}

	public LocalDateTime getProcessedAt() {
		return processedAt;
	}

	public void setProcessedAt(LocalDateTime processedAt) {
		this.processedAt = processedAt;
	}

	public String getId() {
		return id;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public String getContentType() {
		return contentType;
	}

	public long getFileSizeBytes() {
		return fileSizeBytes;
	}

	public LocalDateTime getUploadedAt() {
		return uploadedAt;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserEmail() {
		return userEmail;
	}
 

}
