package com.webservice.example;

import org.springframework.http.HttpStatus;

public class CustomException extends Exception {
	
		private static final long serialVersionUID = 2L;
	private String message;
	private HttpStatus status;

	public CustomException (String message, HttpStatus status){
		this.message = message;
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}
	
}

