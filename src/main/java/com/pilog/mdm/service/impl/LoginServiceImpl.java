package com.pilog.mdm.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilog.mdm.config.JwtService;
import com.pilog.mdm.dto.CreatePasswordResetResponseDto;
import com.pilog.mdm.dto.EmailResponseDto;
import com.pilog.mdm.dto.PerformPasswordResetRequestDto;
import com.pilog.mdm.dto.PerformPswdResetRequestDto;
import com.pilog.mdm.exception.*;
import com.pilog.mdm.exception.enums.ExceptionMessage;
import com.pilog.mdm.model.*;
import com.pilog.mdm.repository.*;
import com.pilog.mdm.requestbody.AuthRequest;
import com.pilog.mdm.requestbody.AuthResponse;
import com.pilog.mdm.service.EmailNotificationService;
import com.pilog.mdm.service.IOtpGenerator;
import com.pilog.mdm.service.LoginService;
import com.pilog.mdm.service.SPersDetailService;
import com.pilog.mdm.utils.InsightsUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

	private final SPersDetailService sPDService;
	private final AuthenticationManager authMgr;
	private final JwtService jwtService;
    private final SPersAuditRepository auditRepository;
	private final SPersDetailRepository sPDRepo;
	private final IOtpGenerator otpGenerator;
	private final EmailNotificationService emailNotificationService;
	private final PasswdRstRequestRepository passwdRstRequestRepository;
	private final PasswordEncoder passwordEncoder;
	private final SAuthorisationRepository sAuthRepo;
	private final UserDeactivationRepository userDeactivationRepository;

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

	@Override
	public CreatePasswordResetResponseDto createPasswordResetRequest() {
		Optional<SPersDetail> sPersDetail = sPDRepo.findByUserName(InsightsUtils.getCurrentUsername());

		if (sPersDetail.isEmpty()) {
			throw new NotFoundException(HttpStatus.NOT_FOUND, ExceptionMessage.USER_NOT_FOUND.getMessage(), ExceptionMessage.USER_NOT_FOUND.getErrorCode());
		}
		var date = new Date();
		Integer otp = otpGenerator.generateOTP(sPersDetail.get().getEmail());
		PasswdRstRequest passwdRstRequest = new PasswdRstRequest();
		passwdRstRequest.setUserName(sPersDetail.get().getUsername());
		passwdRstRequest.setOtp(otp.toString());
		passwdRstRequest.setIsUsed(false);
		passwdRstRequest.setEmailSentCount(1);
		passwdRstRequest.setCreatedAt(date);
		passwdRstRequest.setUpdatedAt(date);
		emailNotificationService.sendEmail(sPersDetail.get().getEmail(), otp);
		passwdRstRequestRepository.save(passwdRstRequest);
		CreatePasswordResetResponseDto createPasswordResetResponseDto = new CreatePasswordResetResponseDto();
		createPasswordResetResponseDto.setMessage("The password reset request was created successfully");
		createPasswordResetResponseDto.setPasswdRstRequestId(passwdRstRequest.getId());
		return createPasswordResetResponseDto;
	}

	@Override
	public EmailResponseDto performPasswordReset(PerformPasswordResetRequestDto performPasswordResetRequestDto) {
		String loggedInUserName = InsightsUtils.getCurrentUsername();
		Optional<PasswdRstRequest> passwdRstRequest = passwdRstRequestRepository.findByUserNameAndOtp(loggedInUserName, performPasswordResetRequestDto.getOtp());
		if (passwdRstRequest.isEmpty()) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Otp appears to be incorrect", "token.is.incorrect");
		}
		if (Boolean.TRUE.equals(passwdRstRequest.get().getIsUsed())) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Otp appears to be incorrect", "token.is.incorrect");
		}

		Optional<SPersDetail> user = sPDRepo.findByUserName(passwdRstRequest.get().getUserName());
		if (user.isEmpty()) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "PasswdRstRequest userId does not exists", "password.request.not.exist");
		}
		if (passwordEncoder.matches(performPasswordResetRequestDto.getPassword().strip() + user.get().getUsername().toUpperCase(), user.get().getPassword())) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "New Password should not be same as old passwords", "password.is.same.as.old");
		}

		if (performPasswordResetRequestDto.getPassword().length() > 16) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Password should be less than or equal to 16 characters", "not.equal.16.characters");
		}
		SAuthorisation sAuthorisation = user.get().getSAuthorisations().stream().findFirst().orElse(null);
		if (sAuthorisation != null) {
			sAuthRepo.updatePassPhrase(sAuthorisation.getSPersDetail().getPersId(), passwordEncoder.encode(performPasswordResetRequestDto.getPassword().strip() + user.get().getUsername().toUpperCase()));
		}

		passwdRstRequest.get().setIsUsed(true);
		passwdRstRequestRepository.save(passwdRstRequest.get());
		EmailResponseDto emailResponseDto = new EmailResponseDto();
		emailResponseDto.setMessage("The password was reset successfully");
		return emailResponseDto;
	}

	@Override
	public void deactivateUser( ) {
		String username = InsightsUtils.getCurrentUsername();

		SPersDetail user = sPDRepo.findByUserName(username)
				.orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, ExceptionMessage.USER_NOT_FOUND.getMessage(), ExceptionMessage.USER_NOT_FOUND.getErrorCode()));

		UserDeactivation deactivation = new UserDeactivation();
		deactivation.setUserName(user.getUsername());
		deactivation.setAuditId("USER_DEACTIVE_"+InsightsUtils.generateId());
		deactivation.setActive(false);
		deactivation.setDeactivationDate(LocalDateTime.now());

		userDeactivationRepository.save(deactivation);
	}

	public Optional<UserDeactivation> getDeactivatedUser(String username){
		return userDeactivationRepository.findByUserName(username);
	}

	@Override
	public EmailResponseDto performPswdReset(PerformPswdResetRequestDto performPswdResetRequestDto) {
		String loggedInUserName = InsightsUtils.getCurrentUsername();

		Optional<SPersDetail> user = sPDRepo.findByUserName(loggedInUserName);
		if (user.isEmpty()) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "PasswdRstRequest userId does not exists", "password.request.not.exist");
		}
		if (!passwordEncoder.matches(performPswdResetRequestDto.getOldPassword().strip() + user.get().getUsername().toUpperCase(), user.get().getPassword())) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Incorrect Old Password", "incorrect.old.password");
		}

		if (performPswdResetRequestDto.getNewPassword().length() > 16) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Password should be less than or equal to 16 characters", "not.equal.16.characters");
		}
		SAuthorisation sAuthorisation = user.get().getSAuthorisations().stream().findFirst().orElse(null);
		if (sAuthorisation != null) {
			sAuthRepo.updatePassPhrase(sAuthorisation.getSPersDetail().getPersId(), passwordEncoder.encode(performPswdResetRequestDto.getNewPassword().strip() + user.get().getUsername().toUpperCase()));
		}
		EmailResponseDto emailResponseDto = new EmailResponseDto();
		emailResponseDto.setMessage("The password was reset successfully");
		return emailResponseDto;
	}


}