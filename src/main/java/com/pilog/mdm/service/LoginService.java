package com.pilog.mdm.service;

import com.pilog.mdm.dto.CreatePasswordResetResponseDto;
import com.pilog.mdm.dto.EmailResponseDto;
import com.pilog.mdm.dto.PerformPasswordResetRequestDto;
import com.pilog.mdm.dto.PerformPswdResetRequestDto;
import com.pilog.mdm.model.UserDeactivation;
import com.pilog.mdm.requestbody.AuthRequest;
import com.pilog.mdm.requestbody.AuthResponse;
import org.springframework.http.HttpHeaders;

import java.util.Optional;


public interface LoginService {


	AuthResponse authenticate(AuthRequest loginRequest, HttpHeaders headers);

	CreatePasswordResetResponseDto createPasswordResetRequest();

	EmailResponseDto performPasswordReset(PerformPasswordResetRequestDto performPasswordResetRequestDto);

    void deactivateUser();
	Optional<UserDeactivation> getDeactivatedUser(String userName);

	EmailResponseDto performPswdReset(PerformPswdResetRequestDto performPswdResetRequestDto);
}
