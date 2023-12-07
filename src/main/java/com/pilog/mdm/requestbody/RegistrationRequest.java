package com.pilog.mdm.requestbody;

import com.pilog.mdm.model.CommonFields;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest extends CommonFields {
	private String auditId;
	private String persId;
	private String country;
	private String defaultInd;
	@NotBlank(message = "Name cannot be blank")
	private String firstName;
	private String lastName;
	@NotBlank(message = "Username cannot be blank")
	private String userName;
	@Pattern(regexp = "^(?=.*[0-9]).{8,}$", message = "Invalid password pattern")
	@Size(min = 6, message = "Password must be at least 6 characters long")
	private String password;
	@NotBlank(message = "Role cannot be blank")
	private String role;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	private String email;

	@Pattern(regexp = "\\d{10}", message = "Mobile number must be 10 digits")
	private String mobile;
}
