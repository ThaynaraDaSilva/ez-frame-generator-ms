package br.duosilva.tech.solutions.ez.frame.generator.ms.adapters.out.http;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import br.duosilva.tech.solutions.ez.frame.generator.ms.application.dto.VideoIngestionRequestDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(name = "videoIngestionClient", url = "${microservice.video-ingestion-endpoint}")
public interface VideoIngestionHttpClient {

	@PostMapping ("/videos/{videoId}/update-status")
	VideoIngestionRequestDto updateVideoProcessingStatus(
		    @PathVariable String videoId,
		    @RequestBody VideoIngestionRequestDto dto
		);
}
