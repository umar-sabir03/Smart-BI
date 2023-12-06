package com.pilog.mdm.controller;

import com.pilog.mdm.requestbody.AuthRequest;
import com.pilog.mdm.requestbody.AuthResponse;
import com.pilog.mdm.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
public class AuthLoginontroller {

	private final LoginService loginSer;
	@PostMapping(value = "/login")
	public ResponseEntity<AuthResponse>login(@RequestBody AuthRequest loginRequest){
		return new ResponseEntity<>(loginSer.authenticate(loginRequest),HttpStatus.OK);

	}


}
