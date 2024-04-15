package com.pilog.mdm.dto;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class PerformPasswordResetRequestDto {
    @NotNull(message = "Otp must not be null or blank.")
    @Size(min = 6, max = 6, message = "Otp must be exactly 6 characters long.")
    private String otp;
    @NotBlank(message = "Password must not be null or blank.")
    private String password;
}
