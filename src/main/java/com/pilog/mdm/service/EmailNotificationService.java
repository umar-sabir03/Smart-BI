package com.pilog.mdm.service;

public interface EmailNotificationService {

	String sendEmail(String email,Integer otp);
	void sendEmailVerification(String userEmail);
}
