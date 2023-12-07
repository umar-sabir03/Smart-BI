package com.pilog.mdm.requestbody;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class AuthRequest {
	@NotBlank(message = "username cannot be blank")
	private String username;
	@NotBlank(message = "Please enter password")
	private String password;
}
