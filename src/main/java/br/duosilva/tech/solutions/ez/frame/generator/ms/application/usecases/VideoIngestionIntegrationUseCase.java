package br.duosilva.tech.solutions.ez.frame.generator.ms.application.usecases;

import org.springframework.stereotype.Component;

import br.duosilva.tech.solutions.ez.frame.generator.ms.adapters.out.http.VideoIngestionHttpClient;
import br.duosilva.tech.solutions.ez.frame.generator.ms.application.dto.VideoIngestionRequestDto;

@Component
public class VideoIngestionIntegrationUseCase {

	private final VideoIngestionHttpClient videoIngestionHttpClient;	
	

	public VideoIngestionIntegrationUseCase(VideoIngestionHttpClient videoIngestionHttpClient) {
		super();
		this.videoIngestionHttpClient = videoIngestionHttpClient;
	}



	public void updateVideoProcessingStatus(String videoId, String processingStatus, String presignedUrl) {
		VideoIngestionRequestDto dto =   new VideoIngestionRequestDto(); 
		if (processingStatus == "COMPLETED") {
			dto.setStatus("COMPLETED");
			dto.setResultObjectKey(presignedUrl);
		} else {

			dto.setStatus("FAILED");
			dto.setResultObjectKey(null);
			dto.setErrorMessage("NÃO FOI POSSIVEL PROCESSAR O VIDEO");
		}
		
		videoIngestionHttpClient.updateVideoProcessingStatus(videoId,dto);

	}
}
