package com.pilog.mdm.requestdto;

import lombok.Data;

@Data
public class VerifyEmailDTO {
    private String email;
    private Integer otp;
}