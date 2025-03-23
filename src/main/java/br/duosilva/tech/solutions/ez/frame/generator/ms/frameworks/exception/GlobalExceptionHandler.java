package br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	  @ExceptionHandler(BusinessRuleException.class)
	    public ResponseEntity<String> handleBusinessRuleException(BusinessRuleException ex) {
	        return ResponseEntity
	                .unprocessableEntity()
	                .body(ex.getMessage()); // HTTP 422
	    }

	    @ExceptionHandler(IllegalArgumentException.class)
	    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
	        return ResponseEntity
	                .badRequest()
	                .body(ex.getMessage()); // HTTP 400
	    }

	    @ExceptionHandler(Exception.class)
	    public ResponseEntity<String> handleGenericException(Exception ex) {
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Unexpected error: " + ex.getMessage()); // HTTP 500
	    }

}
