package com.pilog.mdm.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilog.mdm.config.JwtService;
import com.pilog.mdm.exception.CustomJsonProcessingException;
import com.pilog.mdm.exception.CustomMissingHeaderException;
import com.pilog.mdm.exception.InvalidUsernameException;
import com.pilog.mdm.model.SPersAudit;
import com.pilog.mdm.repository.SPersAuditRepository;
import com.pilog.mdm.requestbody.AuthRequest;
import com.pilog.mdm.requestbody.AuthResponse;
import com.pilog.mdm.service.LoginService;
import com.pilog.mdm.service.SPersDetailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

	private final SPersDetailService sPDService;
	private final AuthenticationManager authMgr;
	private final JwtService jwtService;
    private final SPersAuditRepository auditRepository;

	private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);


	@Override
	public AuthResponse authenticate(AuthRequest loginRequest, HttpHeaders headers) {
		AuthResponse authResponse=new AuthResponse();
				logger.info("Attempting to authenticate user: {}", loginRequest.getUsername());
				authentication(loginRequest.getUsername(), loginRequest.getPassword());
				UserDetails userDetails = sPDService.loadUserByUsername(loginRequest.getUsername());
				String token = jwtService.generateToken(userDetails);
				authResponse.setToken(token);
				authResponse.setMessage("Success");
				logger.info("User {} authenticated successfully", loginRequest.getUsername());
				saveLoginInfo(loginRequest,headers,token );

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

	private void saveLoginInfo(AuthRequest loginRequest, HttpHeaders headers,String token ) {
		SPersAudit sPersAudit = new SPersAudit();
		String persId = sPDService.getUserByUsername(loginRequest.getUsername()).getPersId();
		sPersAudit.setPersId(persId);
		ObjectMapper objectMapper = new ObjectMapper();
			String deviceInfo = headers.getFirst("Device-Info");
		if (deviceInfo != null && !deviceInfo.isEmpty()) {
			try {
				JsonNode jsonNode = objectMapper.readTree(deviceInfo);
				String ipAddress = jsonNode.get("IP Address").asText();
				String deviceName = jsonNode.get("Device Name").asText();
				sPersAudit.setBrowser("Mobile_Application");
				sPersAudit.setDeviceName(deviceName);
				sPersAudit.setIpAddress(ipAddress);
				sPersAudit.setSessionId(token);
				sPersAudit.setFlag("N");
				sPersAudit.setCreateBy(loginRequest.getUsername());
				LocalDateTime loginDate=LocalDateTime.now();
				sPersAudit.setLoginDate(loginDate);
				sPersAudit.setEditBy(loginRequest.getUsername());
					auditRepository.save(sPersAudit);

				logger.info("Users {} Login Details Saved successfully", sPersAudit);
			} catch (JsonProcessingException e) {
				throw new CustomJsonProcessingException("Error processing JSON", e);
			}
		} else {
			throw new CustomMissingHeaderException("Device-Info header is missing or empty");
		}
	}

}