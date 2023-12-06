package com.pilog.mdm.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler {

	@ExceptionHandler(InvalidUsernameException.class)
	public ResponseEntity<RestExceptionStatusResponse> handleInvalidUsernameException(InvalidUsernameException ex) {
		RestExceptionStatusResponse e = new RestExceptionStatusResponse();
		e.setStatus(HttpStatus.BAD_REQUEST.value());
		e.setMessage(ex.getMessage());
		return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
	}

}
