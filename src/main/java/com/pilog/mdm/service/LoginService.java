package com.pilog.mdm.service;

import com.pilog.mdm.requestbody.AuthRequest;
import com.pilog.mdm.requestbody.AuthResponse;


public interface LoginService {


	AuthResponse authenticate(AuthRequest loginRequest);

}
