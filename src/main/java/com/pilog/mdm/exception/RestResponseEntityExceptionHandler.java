package com.pilog.mdm.exception;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler {
	private static final Logger logger = LogManager.getLogger(RestResponseEntityExceptionHandler.class);
	@ExceptionHandler(InvalidUsernameException.class)
	public ResponseEntity<RestExceptionStatusResponse> handleInvalidUsernameException(InvalidUsernameException ex) {
		RestExceptionStatusResponse e = new RestExceptionStatusResponse();
		e.setStatus(HttpStatus.BAD_REQUEST.value());
		e.setMessage(ex.getMessage());
		return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler
	public ResponseEntity<RestExceptionStatusResponse> handleRegistrationException(RegistrationException regEx){
		RestExceptionStatusResponse e = new RestExceptionStatusResponse();
		e.setStatus(HttpStatus.BAD_REQUEST.value());
		e.setMessage(regEx.getMessage());
		return new ResponseEntity<RestExceptionStatusResponse>(e,HttpStatus.BAD_REQUEST);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(
			MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return errors;
	}
	@ExceptionHandler
	public ResponseEntity<RestExceptionStatusResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
		logger.error("ResourceNotFoundException: {}", ex.getMessage(), ex);
		RestExceptionStatusResponse response = createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}
	private RestExceptionStatusResponse createErrorResponse(HttpStatus status, String message) {
		RestExceptionStatusResponse response = new RestExceptionStatusResponse();
		response.setStatus(status.value());
		response.setMessage(message);
		return response;
	}
}
