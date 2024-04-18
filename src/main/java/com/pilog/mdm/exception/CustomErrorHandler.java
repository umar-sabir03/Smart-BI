package com.pilog.mdm.exception;

import com.pilog.mdm.exception.enums.ExceptionMessage;
import com.pilog.mdm.utils.InsightsUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomErrorHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getErrorResponse(ExceptionMessage.INERNAL_SERVER_ERROR.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), ExceptionMessage.INERNAL_SERVER_ERROR.getErrorCode()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(NotFoundException notFoundException) {
        return ResponseEntity.status(notFoundException.getStatusCode()).body(getErrorResponse(notFoundException.getMessage(), notFoundException.getRawStatusCode(), notFoundException.getStatusText()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(BadRequestException badRequestException) {
        return ResponseEntity.status(badRequestException.getStatusCode()).body(getErrorResponse(badRequestException.getMessage(), badRequestException.getRawStatusCode(), badRequestException.getStatusText()));
    }

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ErrorResponseDto> handleUserServiceException(UserServiceException userServiceException) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getErrorResponse(userServiceException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), ExceptionMessage.USER_NOT_DELETED.getErrorCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ErrorResponseDto errors = ex.getBindingResult().getFieldErrors().stream().filter(fieldError -> fieldError.getDefaultMessage() != null).map(this::getReadableErrorMessage).findFirst().orElse(ErrorResponseDto.builder().errorCode("NaN").message("NaN").timestamp(InsightsUtils.getCurrentDateTime()).build());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request body is missing or invalid.");
    }


    public static ErrorResponseDto getErrorResponse(String message, Integer statusCode, String errorCode) {
        return ErrorResponseDto.builder().message(message).timestamp(InsightsUtils.getCurrentDateTime()).status(statusCode).errorCode(errorCode).build();
    }

    private ErrorResponseDto getReadableErrorMessage(FieldError fieldError) {

        return ErrorResponseDto.builder().message(fieldError.getDefaultMessage()).timestamp(InsightsUtils.getCurrentDateTime()).status(HttpStatus.BAD_REQUEST.value()).errorCode(fieldError.toString()).build();
    }

    @ExceptionHandler({CustomJsonProcessingException.class, CustomMissingHeaderException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleJsonProcessingException(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RegistrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestExceptionStatusResponse> handleRegistrationException(RegistrationException regEx) {
        RestExceptionStatusResponse e = new RestExceptionStatusResponse();
        e.setStatus(HttpStatus.BAD_REQUEST.value());
        e.setMessage(regEx.getMessage());
        return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidUsernameException.class)
    public ResponseEntity<RestExceptionStatusResponse> handleInvalidUsernameException(InvalidUsernameException ex) {
        RestExceptionStatusResponse e = new RestExceptionStatusResponse();
        e.setStatus(HttpStatus.BAD_REQUEST.value());
        e.setMessage(ex.getMessage());
        return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<RestExceptionStatusResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
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
