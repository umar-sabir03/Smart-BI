package com.pilog.mdm.controller;

import com.pilog.mdm.requestdto.VerifyEmailDTO;
import com.pilog.mdm.service.EmailNotificationService;
import com.pilog.mdm.service.IOtpGenerator;
import com.pilog.mdm.service.ISPersProfileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/${api-version}")
@RequiredArgsConstructor
public class EmailNotificationsController {

	private final EmailNotificationService emailService;
	private final IOtpGenerator otpGenerator;
	private final ISPersProfileService profileService;
	private static final Logger logger = LoggerFactory.getLogger(EmailNotificationsController.class);


	@GetMapping(value = "/sendOtpToEmail")
	public ResponseEntity<Map<String,String>> sendOtpToEmail(@Validated @RequestParam("email") String email) {
		Map<String,String> resp=new HashMap<>();
		try {
			String message = emailService.sendEmail(email);
			resp.put("message", message);
			logger.info("Email sent successfully to: {}", email);
			return new ResponseEntity<>(resp, HttpStatus.OK);
		} catch (Exception e) {
			resp.put("message", "Error sending email");
			logger.error("Error sending email to {}: {}", email, e.getMessage(), e);
			return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PostMapping(value = "/verifyemail")
	public ResponseEntity<Map<String,String>> verifyOtp( @RequestBody VerifyEmailDTO verifyEmailDTO)
	{
		Map<String,String> result=new HashMap<>();
		String userEmail = verifyEmailDTO.getEmail();
		Integer otp =verifyEmailDTO.getOtp();
		boolean isOtpValid = otpGenerator.validateOTP(userEmail, otp);
		if (!isOtpValid) {
			result.put("message","Invalid OTP");
			logger.warn("Invalid OTP received for email: {}", userEmail);
			return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
		}
		try {
		emailService.sendEmailVerification(userEmail);
			result.put("message","Email Verified Successfully");
			logger.info("Email verification successful for: {}", userEmail);

		}catch (Exception ex){
			logger.error("Error verifying email for {}: {}", userEmail, ex.getMessage(), ex);
			ex.printStackTrace();
			result.put("error", ex.getMessage());
		return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);
    	}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@GetMapping(value = "/roles")
	public ResponseEntity<List<String>> getroles(){
		List<String> resp;
		try {
			resp = profileService.getAllRoles();
			logger.info("Successfully retrieved roles: {}", resp);
			return new ResponseEntity<>(resp, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error while fetching roles: {}", e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
