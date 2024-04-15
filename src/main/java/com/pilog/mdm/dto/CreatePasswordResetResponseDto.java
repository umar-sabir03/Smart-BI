package com.pilog.mdm.dto;


import lombok.Data;

@Data
public class CreatePasswordResetResponseDto {
    private String message;
    private String passwdRstRequestId;
}
