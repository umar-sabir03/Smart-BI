package com.pilog.mdm.service;

public interface IOtpGenerator {

     Integer generateOTP(String email);
     Integer getOPTByKey(String email);
     void clearOTPFromCache(String email);
     public Boolean validateOTP(String email, Integer otpNumber);
}
