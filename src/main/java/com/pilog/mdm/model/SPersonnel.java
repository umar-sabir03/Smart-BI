package com.pilog.mdm.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "S_PERSONNEL")
public class SPersonnel extends CommonFields {

    @Column(name = "AUDIT_ID", length = 4000)
    private String auditId;
    @Id
    @Column(name = "PERS_ID", unique = true, nullable = false, columnDefinition = "raw(16)")
    private String persId;
    @Column(name = "STATUS", nullable = false, length = 120)
    private String status;
    @Column(name = "EXPIRY_DATE", nullable = false, length = 7)
    private LocalDate expiryDate;
    @Column(name = "PASSWORD_FLAG", nullable = false, length = 4)
    private String passwordFlag;
    @Column(name = "LOGIN_ATTEMPTS", nullable = false, precision = 22, scale = 0)
    private Integer loginAttempts;
    @Column(name = "ORGN_ID", nullable = false)
    private String orgnId;

}
