package com.pilog.mdm.utils;

import com.pilog.mdm.model.CommonFields;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InsightsUtils {

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	public static String generateId() {
		return RandomStringUtils.randomAlphanumeric(32).toUpperCase();
	}

	public CommonFields setRegistrationsCommonFields(String userName) {
		CommonFields commonFields = new CommonFields();
		LocalDate date = LocalDate.now();
		commonFields.setCreateBy(userName);
		commonFields.setCreateDate(date);
		commonFields.setEditDate(date);
		commonFields.setEditBy(userName);
		return commonFields;
	}

	public String generateRandomHex(int length) {
		byte[] randomBytes = new byte[length / 2];
		SECURE_RANDOM.nextBytes(randomBytes);
		BigInteger number = new BigInteger(1, randomBytes);
		return number.toString(16).toUpperCase();
	}
	
	public <T extends CommonFields> T setMetadata(T metaData, String userName) {
		if (userName == null || "".equalsIgnoreCase(userName)) {
			return null;
		}
		LocalDate date = LocalDate.now();
		metaData.setEditDate(date);
		metaData.setCreateDate(date);
		metaData.setCreateBy(userName);
		metaData.setEditBy(userName);
		return metaData;
	}

	public static String getCurrentUsername() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}
		var userDetails = authentication.getPrincipal();
		if (userDetails instanceof UserDetails dud) {
			return dud.getUsername();
		}
		return null;
	}
	public static String getCurrentDateTime() {
		LocalDateTime now = LocalDateTime.now();
		return now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

}

//byte test2[] = Base64.getDecoder().decode(imgStr);