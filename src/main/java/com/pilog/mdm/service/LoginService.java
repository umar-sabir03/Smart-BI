package com.pilog.mdm.service;

import com.pilog.mdm.requestbody.AuthRequest;
import com.pilog.mdm.requestbody.AuthResponse;
import org.springframework.http.HttpHeaders;


public interface LoginService {


	AuthResponse authenticate(AuthRequest loginRequest, HttpHeaders headers);

}
