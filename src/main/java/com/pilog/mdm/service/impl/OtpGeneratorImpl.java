package com.pilog.mdm.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pilog.mdm.service.IOtpGenerator;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Description(value = "Service for generating and validating OTP.")
@Service
public class OtpGeneratorImpl implements IOtpGenerator {

    private static final Integer EXPIRE_MIN = 5;
    private LoadingCache<String, Integer> otpCache;

    public OtpGeneratorImpl()
    {
        super();
        otpCache = CacheBuilder.newBuilder()
                .expireAfterWrite(EXPIRE_MIN, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String s) throws Exception {
                        return 0;
                    }
                });
    }
    public Integer generateOTP(String email)
    {
        Random random = new Random();
        int OTP = 100000 + random.nextInt(900000);
        otpCache.put(email, OTP);
        return OTP;
    }

    public Integer getOPTByKey(String key)
    {
        return otpCache.getIfPresent(key);
    }


    public void clearOTPFromCache(String key) {
        otpCache.invalidate(key);
    }
    public Boolean validateOTP(String email, Integer otpNumber)
    {
        // get OTP from cache
        Integer cacheOTP = getOPTByKey(email);
        if (cacheOTP!=null && cacheOTP.equals(otpNumber))
        {
            clearOTPFromCache(email);
            return true;
        }
        return false;
    }
}
