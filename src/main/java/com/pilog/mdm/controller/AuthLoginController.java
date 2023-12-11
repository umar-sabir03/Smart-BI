package com.pilog.mdm.controller;

import com.pilog.mdm.requestbody.AuthRequest;
import com.pilog.mdm.requestbody.AuthResponse;
import com.pilog.mdm.requestbody.RegistrationRequest;
import com.pilog.mdm.service.LoginService;
import com.pilog.mdm.service.RegistrationService;
import lombok.RequiredArgsConstructor;
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
	@PostMapping(value = "/login")
	public ResponseEntity<AuthResponse>login(@Valid @RequestBody AuthRequest loginRequest){


		return new ResponseEntity<>(loginSer.authenticate(loginRequest),HttpStatus.OK);
	}

	@PostMapping(value = "/register")
	public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegistrationRequest request) {
		return new ResponseEntity<>(regSer.registerUser(request), HttpStatus.CREATED);
	}
}
