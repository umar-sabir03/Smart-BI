package com.pilog.mdm.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreatePasswordResetRequestDto {
    @NotBlank(message = "email must not be null or blank.")
    private String userName;
}
