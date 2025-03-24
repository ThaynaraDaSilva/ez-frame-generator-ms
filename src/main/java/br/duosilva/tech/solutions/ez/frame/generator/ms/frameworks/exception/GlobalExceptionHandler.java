package br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


	@ExceptionHandler(BusinessRuleException.class)
	public ResponseEntity<ErrorResponse> handleBusinessRuleException(BusinessRuleException ex) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
		return ResponseEntity.badRequest().body(errorResponse);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGenericException(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage()); // HTTP
																														// 500
	}

}
