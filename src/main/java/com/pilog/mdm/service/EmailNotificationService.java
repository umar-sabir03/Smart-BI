package com.pilog.mdm.service;

public interface EmailNotificationService {

	String sendEmail(String email);
	void sendEmailVerification(String userEmail);
}
