package com.pilog.mdm.controller;

import com.pilog.mdm.requestbody.AuthRequest;
import com.pilog.mdm.requestbody.AuthResponse;
import com.pilog.mdm.requestbody.RegistrationRequest;
import com.pilog.mdm.service.LoginService;
import com.pilog.mdm.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
public class AuthLoginController {

	private final LoginService loginSer;

	private final RegistrationService regSer;
	private static final Logger logger = LoggerFactory.getLogger(AuthLoginController.class);


	@PostMapping(value = "/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest loginRequest,
											  @RequestHeader HttpHeaders headers) {
			AuthResponse authResponse = loginSer.authenticate(loginRequest,headers);
			logger.info("User {} successfully logged in", loginRequest.getUsername());
			return new ResponseEntity<>(authResponse, HttpStatus.OK);
	}
	@PostMapping(value = "/register")
	public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegistrationRequest request) {
			AuthResponse authResponse = regSer.registerUser(request);
			logger.info("User {} successfully registered", request.getUserName());
			return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
	}
}
