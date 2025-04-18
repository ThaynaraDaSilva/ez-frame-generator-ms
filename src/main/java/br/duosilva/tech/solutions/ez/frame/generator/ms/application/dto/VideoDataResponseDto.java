package br.duosilva.tech.solutions.ez.frame.generator.ms.application.dto;

public class VideoDataResponseDto {

	private String videoId;
	private String originalFileName;
	private String s3BucketName;
	private String s3Key;
	private String uploadTimestamp;
	private String userId;
	private String userEmai;

	public VideoDataResponseDto(String videoId, String originalFileName, String s3BucketName, String s3Key,
			String uploadTimestamp, String userId, String userEmai) {
		super();
		this.videoId = videoId;
		this.originalFileName = originalFileName;
		this.s3BucketName = s3BucketName;
		this.s3Key = s3Key;
		this.uploadTimestamp = uploadTimestamp;
		this.userId = userId;
		this.userEmai = userEmai;
	}

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

	public String getS3BucketName() {
		return s3BucketName;
	}

	public void setS3BucketName(String s3BucketName) {
		this.s3BucketName = s3BucketName;
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

	public String getUserEmai() {
		return userEmai;
	}

	public void setUserEmai(String userEmai) {
		this.userEmai = userEmai;
	}

	@Override
	public String toString() {
		return "VideoDataResponseDTO [videoId=" + videoId + ", originalFileName=" + originalFileName + ", s3BucketName="
				+ s3BucketName + ", s3Key=" + s3Key + ", uploadTimestamp=" + uploadTimestamp + ", userId=" + userId
				+ ", userEmai=" + userEmai + "]";
	}

}
