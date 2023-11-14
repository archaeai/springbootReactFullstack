package com.jusuck.nbsSecurity.exception;

public class EmailAlreadyExistsException extends RuntimeException{
	public EmailAlreadyExistsException(String message) {
		super(message);
	}
}
