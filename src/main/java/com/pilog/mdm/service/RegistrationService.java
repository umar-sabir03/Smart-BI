package com.pilog.mdm.service;

import com.pilog.mdm.requestbody.AuthResponse;
import com.pilog.mdm.requestbody.RegistrationRequest;

public interface RegistrationService {

	AuthResponse registerUser(RegistrationRequest request);
}
