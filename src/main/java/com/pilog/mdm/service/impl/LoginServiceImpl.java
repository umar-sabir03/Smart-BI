package com.pilog.mdm.service.impl;

import com.pilog.mdm.config.JwtService;
import com.pilog.mdm.exception.InvalidUsernameException;
import com.pilog.mdm.requestbody.AuthRequest;
import com.pilog.mdm.requestbody.AuthResponse;
import com.pilog.mdm.service.LoginService;
import com.pilog.mdm.service.SPersDetailService;
import lombok.RequiredArgsConstructor;
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

	@Override
	public AuthResponse authenticate(AuthRequest loginRequest) {
		AuthResponse authResponse=new AuthResponse();
		authentication(loginRequest.getUsername(),loginRequest.getPassword());
		UserDetails userDetails = sPDService.loadUserByUsername(loginRequest.getUsername());
		String token = jwtService.generateToken(userDetails);
			 authResponse.setToken(token);
			 authResponse.setMessage("Success");
		return authResponse;
	}
    	private void authentication(String username, String password) {
			String encodePassword = password+ (username.toUpperCase());
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(username, encodePassword);
		try {
			authMgr.authenticate(usernamePasswordAuthenticationToken);
		} catch (BadCredentialsException e) {
			e.printStackTrace();
			throw new InvalidUsernameException("Invalid username & password!!");
		}

	}

}