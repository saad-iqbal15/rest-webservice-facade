package com.webservice.example;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExcepHandler {

	@ExceptionHandler
	public ResponseEntity<String> exceptionMessage (CustomException exception) throws JSONException {
		
		JSONObject object = new JSONObject();
		object.put("ErrorMessage", exception.getMessage());
		object.put("HttpStatus", exception.getStatus());
		
		
		return new ResponseEntity<>(object.toString(),exception.getStatus());
		
	}
	
}

