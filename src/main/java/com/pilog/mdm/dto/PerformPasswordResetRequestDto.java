package com.pilog.mdm.dto;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class PerformPasswordResetRequestDto {
    @NotNull(message = "Otp must not be null or blank.")
    @Size(min = 6, max = 6, message = "Otp must be exactly 6 characters long.")
    private String otp;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace, and must be at least 8 characters long.")
  //  @Size(min = 8, message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace, and must be at least 8 characters long.")
    private String password;


}
