package com.pilog.mdm.service.impl;

import com.pilog.mdm.config.JwtService;
import com.pilog.mdm.exception.InvalidUsernameException;
import com.pilog.mdm.requestbody.AuthRequest;
import com.pilog.mdm.requestbody.AuthResponse;
import com.pilog.mdm.service.LoginService;
import com.pilog.mdm.service.SPersDetailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

	private final SPersDetailService sPDService;
	private final AuthenticationManager authMgr;
	private final JwtService jwtService;

	private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);


	@Override
	public AuthResponse authenticate(AuthRequest loginRequest) {
		AuthResponse authResponse=new AuthResponse();
		try {
			logger.info("Attempting to authenticate user: {}", loginRequest.getUsername());
			authentication(loginRequest.getUsername(),loginRequest.getPassword());
		UserDetails userDetails = sPDService.loadUserByUsername(loginRequest.getUsername());
		String token = jwtService.generateToken(userDetails);
			 authResponse.setToken(token);
			 authResponse.setMessage("Success");
			logger.info("User {} authenticated successfully", loginRequest.getUsername());
		} catch (Exception e) {
			logger.error("Error during authentication for user {}: {}", loginRequest.getUsername(), e.getMessage(), e);
			authResponse.setMessage("Authentication failed");
		}
		return authResponse;
	}
    	private void authentication(String username, String password) {
			try {
			String encodePassword = password+ (username.toUpperCase());
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(username, encodePassword);
			logger.info("Attempting authentication for user: {}", username);
			authMgr.authenticate(usernamePasswordAuthenticationToken);
		}  catch (BadCredentialsException e) {
				// Log authentication failure
				logger.warn("Authentication failed for user {}: {}", username, e.getMessage(), e);
				throw new InvalidUsernameException("Invalid username & password!!");
			}
	}

}