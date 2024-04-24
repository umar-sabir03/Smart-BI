package com.pilog.mdm.dto;


import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class PerformPswdResetRequestDto {
    @NotNull(message = "Otp must not be null or blank.")
    private String oldPassword;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace, and must be at least 8 characters long.")
  //  @Size(min = 8, message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace, and must be at least 8 characters long.")
    private String newPassword;


}
