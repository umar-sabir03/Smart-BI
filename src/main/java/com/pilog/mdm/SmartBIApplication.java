package com.pilog.mdm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartBIApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartBIApplication.class, args);
		System.out.println("jwt-security initiated");
	}
}
