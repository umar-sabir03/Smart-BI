package com.pilog.mdm.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "S_Pers_Audit")
@Data
public class SPersAudit {
    @Column(name = "PERS_ID")
    private String persId;
    @Id
    @Column(name = "SESSION_ID", length = 150, nullable = false)
    private String sessionId;

    @Column(name = "IP_ADDRESS", length = 15, nullable = false)
    private String ipAddress;

    @Column(name = "BROWSER", length = 150, nullable = false)
    private String browser;

    @Column(name = "DEVICE_NAME", length = 50, nullable = false)
    private String deviceName;

    @Column(name = "LOGIN_DATE", nullable = false, updatable = false, columnDefinition = "DATE DEFAULT SYSDATE")
    private LocalDateTime loginDate;

    @Column(name = "LOGOUT_DATE")
    private LocalDateTime logoutDate;

    @Column(name = "FLAG", length = 1, nullable = false)
    private String flag;
    @Column(name = "CREATE_BY", length = 50, nullable = false)
    private String createBy;

    @Column(name = "EDIT_BY", length = 50, nullable = false)
    private String editBy;

}