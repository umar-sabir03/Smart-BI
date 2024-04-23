package com.pilog.mdm.controller;

import com.pilog.mdm.dto.CreatePasswordResetResponseDto;
import com.pilog.mdm.dto.EmailResponseDto;
import com.pilog.mdm.dto.PerformPasswordResetRequestDto;
import com.pilog.mdm.model.UserDeactivation;
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
import java.util.Optional;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
public class AuthLoginController {

	private final LoginService loginSer;

	private final RegistrationService regSer;
	private static final Logger logger = LoggerFactory.getLogger(AuthLoginController.class);


	@PostMapping(value = "/login")
	public ResponseEntity<?> login(@Valid @RequestBody AuthRequest loginRequest,
											  @RequestHeader HttpHeaders headers) {
		Optional<UserDeactivation> userDeactivation = loginSer.getDeactivatedUser(loginRequest.getUsername());
		if (userDeactivation.isPresent() && !userDeactivation.get().isActive()) {
			String message = "User is deactivated. Please contact the administrator for reactivation.";
			logger.info("User '{}' is deactivated. Contact administrator for reactivation.", loginRequest.getUsername());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
		}

		AuthResponse authResponse = loginSer.authenticate(loginRequest, headers);
		logger.info("User {} successfully logged in", loginRequest.getUsername());
		return new ResponseEntity<>(authResponse, HttpStatus.OK);
	}

	@PostMapping(value = "/register")
	public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegistrationRequest request) {
		AuthResponse authResponse = regSer.registerUser(request);
		logger.info("User {} successfully registered", request.getUserName());
		return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
	}

	/**
	 * @param createPasswordResetRequest
	 * @return CreatePasswordResetResponseDto
	 */
	@PostMapping(path = "/rquest/passwordreset")
	public CreatePasswordResetResponseDto createPasswordResetRequest() {
		return loginSer.createPasswordResetRequest();
	}

	/**
	 * @param performPasswordResetRequestDto
	 * @return EmailResponseDto
	 */
	@PostMapping(path = "/perform/passwordreset")
	public EmailResponseDto performPasswordReset(@Valid @RequestBody PerformPasswordResetRequestDto performPasswordResetRequestDto) {

		return loginSer.performPasswordReset(performPasswordResetRequestDto);
	}

	@PostMapping("/perform/deactivate")
	public ResponseEntity<String> deactivateUser() {
		loginSer.deactivateUser();
		return ResponseEntity.ok("User deactivated successfully");
	}
}
