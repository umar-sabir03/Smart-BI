package com.pilog.mdm.exception;

public class RestExceptionStatusResponse {
	private int status;
	private String message;	

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String string) {
		this.message = string;
	}
}
