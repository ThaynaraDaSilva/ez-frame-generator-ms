package br.duosilva.tech.solutions.ez.frame.generator.ms.frameworks.exception;

public class BusinessRuleException extends RuntimeException{
	
	public BusinessRuleException(String message) {
        super(message);
    }

    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }

}
