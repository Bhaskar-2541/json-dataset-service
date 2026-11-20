package com.project.exception;

public class InvalidQueryException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public InvalidQueryException(String message) {
        super(message);
    }
}
