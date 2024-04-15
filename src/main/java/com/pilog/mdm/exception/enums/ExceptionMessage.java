package com.pilog.mdm.exception.enums;

public enum ExceptionMessage {
    INERNAL_SERVER_ERROR("Internal server error", "internal.server.error"), USER_NOT_DELETED("User is not deleted.", "user.is.not.deleted"), USER_NOT_FOUND("User is not found.", "user.is.not.found");

    private final String message;
    private final String errorCode;

    ExceptionMessage(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorCode() {
        return errorCode;
    }
}


