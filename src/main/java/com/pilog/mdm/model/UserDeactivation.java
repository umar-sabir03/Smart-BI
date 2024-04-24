package com.pilog.mdm.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_deactivation")
@Data
public class UserDeactivation {

    @Id
    @Column(name = "userName")
    private String userName;

    @Column(name = "AUDIT_ID",unique = true,nullable = false)
    private String auditId;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "deactivation_date")
    private LocalDateTime deactivationDate;

}
