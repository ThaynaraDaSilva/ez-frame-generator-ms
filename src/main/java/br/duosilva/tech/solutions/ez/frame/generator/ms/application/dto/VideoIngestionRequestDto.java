package br.duosilva.tech.solutions.ez.frame.generator.ms.application.dto;

public class VideoIngestionRequestDto {

	private String status;
	private String resultObjectKey;
	private String errorMessage;
	
	
	
	
	public VideoIngestionRequestDto() {
		super();
	}


	public VideoIngestionRequestDto(String status, String resultObjectKey, String errorMessage) {
		super();
		this.status = status;
		this.resultObjectKey = resultObjectKey;
		this.errorMessage = errorMessage;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getResultObjectKey() {
		return resultObjectKey;
	}


	public void setResultObjectKey(String resultObjectKey) {
		this.resultObjectKey = resultObjectKey;
	}


	public String getErrorMessage() {
		return errorMessage;
	}


	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}
