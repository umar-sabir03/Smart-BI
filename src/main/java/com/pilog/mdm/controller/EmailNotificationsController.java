package com.pilog.mdm.controller;

import com.pilog.mdm.requestdto.VerifyEmailDTO;
import com.pilog.mdm.service.EmailNotificationService;
import com.pilog.mdm.service.IOtpGenerator;
import com.pilog.mdm.service.ISPersProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

	@GetMapping(value = "/sendOtpToEmail")
	public ResponseEntity<Map<String,String>> sendOtpToEmail(@Validated @RequestParam("email") String email) {
		Map<String,String> resp=new HashMap<>();
		resp.put("message",emailService.sendEmail(email));
		return new ResponseEntity<>(resp, HttpStatus.OK);
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
			return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
		}
		try {
		emailService.sendEmailVerification(userEmail);
			result.put("message","Email Verified Successfully");
	    }catch (Exception ex){
			ex.printStackTrace();
			result.put("error", ex.getMessage());
		return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);
    	}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	@GetMapping(value = "/roles")
	public ResponseEntity<List<String>> getroles(){
		List<String> resp=new ArrayList<>();
		resp = profileService.getAllRoles();
		return new ResponseEntity<>(resp,HttpStatus.OK);
	}


}
