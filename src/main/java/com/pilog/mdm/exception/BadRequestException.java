package com.pilog.mdm.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.Serial;
import java.nio.charset.Charset;

public class BadRequestException extends HttpStatusCodeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BadRequestException(HttpStatus errorCode, String message, String statusText) {
        super(message, errorCode, statusText, (HttpHeaders)null, (byte[])null, (Charset)null);
    }


}

