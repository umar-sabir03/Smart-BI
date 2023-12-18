package com.pilog.mdm.utils;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;


public class PilogEncryption implements PasswordEncoder {

	public String encrypt(String userName, String password, String orgnId) {
		String toBeEncrypt = "";
		String encryptedString = "";
		try {
			toBeEncrypt = password + userName.toUpperCase() + orgnId + "iVision2016-ShellFish";
			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			byte[] hash = digest.digest(toBeEncrypt.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xFF & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			encryptedString = hexString.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedString;
	}
	@Override
	public String encode(CharSequence rawPassword) {
		return encrypt("",rawPassword.toString(), InsightsConstants.ORGN_ID);
	}
	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		String encryptedPassword = encrypt( "",rawPassword.toString(), InsightsConstants.ORGN_ID);
		return encodedPassword.equals(encryptedPassword);
	}
}
