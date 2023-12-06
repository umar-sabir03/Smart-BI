package com.pilog.mdm.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="O_RECORD_VISUALIZATION")
public class ORecordVisualisation {
    @Id
    @Column(name = "AUDIT_ID", length = 100)
    private String auditId;

    @Column(name = "ORGN_ID", columnDefinition = "RAW")
    private byte[] orgnId;

    @Column(name = "ROLE_ID", length = 50)
    private String roleId;

    @Column(name = "X_AXIS_VALUE", length = 1000)
    private String xAxisValue;

    @Column(name = "Y_AXIS_VALUE", length = 1000)
    private String yAxisValue;

    @Column(name = "COMBO_VALUE", length = 1000)
    private String comboValue;

    @Column(name = "Z_AXIS_VALUE", length = 1000)
    private String zAxisValue;

    @Column(name = "DASHBORD_NAME", length = 100)
    private String dashboardName;



}
