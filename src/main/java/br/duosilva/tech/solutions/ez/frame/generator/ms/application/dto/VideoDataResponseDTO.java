package br.duosilva.tech.solutions.ez.frame.generator.ms.application.dto;

public class VideoDataResponseDTO {
	
	private String videoId;
	private String originalFileName;
	private String s3Bucket;
	private String s3Key;
	private String uploadTimestamp;
	private String userId;

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public String getS3Bucket() {
		return s3Bucket;
	}

	public void setS3Bucket(String s3Bucket) {
		this.s3Bucket = s3Bucket;
	}

	public String getS3Key() {
		return s3Key;
	}

	public void setS3Key(String s3Key) {
		this.s3Key = s3Key;
	}

	public String getUploadTimestamp() {
		return uploadTimestamp;
	}

	public void setUploadTimestamp(String uploadTimestamp) {
		this.uploadTimestamp = uploadTimestamp;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "VideoIngestionMessage{" + "videoId='" + videoId + '\'' + ", originalFileName='" + originalFileName
				+ '\'' + ", s3Bucket='" + s3Bucket + '\'' + ", s3Key='" + s3Key + '\'' + ", uploadTimestamp='"
				+ uploadTimestamp + '\'' + ", userId='" + userId + '\'' + '}';
	}


}
