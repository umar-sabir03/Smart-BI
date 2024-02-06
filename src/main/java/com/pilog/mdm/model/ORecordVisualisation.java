package com.pilog.mdm.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="O_RECORD_VISUALIZATION")
public class ORecordVisualisation {
    @Id
    @Column(name = "AUDIT_ID", length = 100)
    private String auditId;

    @Column(name = "ROLE_ID", length = 50)
    private String roleId;

    @Column(name = "X_AXIS_VALUE", length = 1000)
    private String xAxisValue;

    @Column(name = "Y_AXIS_VALUE", length = 1000)
    private String yAxisValue;

    @Column(name = "COMBO_VALUE", length = 1000)
    private String comboValue;

    @Column(name = "CHART_TYPE", length = 22)
    private String chartType;

    @Column(name = "TABLE_NAME", length = 1000)
    private String tableName;

    @Column(name = "CHART_ID", length = 1000)
    private String chartId;

    @Column(name = "AGGREGATE_COLUMNS", length = 1000)
    private String aggregateColumns;

    @Lob
    @Column(name = "CLOB_CHARTPROP")
    private String clobChartProp;

    @Lob
    @Column(name = "CLOB_CHARTDATA")
    private String clobChartData;

    @Lob
    @Column(name = "CLOB_CHARTLAYOUT")
    private String clobChartLayout;

    @Column(name = "CHART_SEQUENCE_NO")
    private Integer chartSequenceNo;

    @Column(name = "FILTER_CONDITION", length = 4000)
    private String filterCondition;

    @Column(name = "CHART_PROPERTIES", length = 4000)
    private String chartProperties;

    @Column(name = "CHART_CONFIG_OBJECT", length = 4000)
    private String chartConfigObject;

    @Column(name = "VISUALIZE_CUST_COL5", length = 1000)
    private String visualizeCustCol5;

    @Column(name = "VISUALIZE_CUST_COL6", length = 1000)
    private String visualizeCustCol6;

    @Column(name = "VISUALIZE_CUST_COL7", length = 1000)
    private String visualizeCustCol7;

    @Column(name = "VISUALIZE_CUST_COL8", length = 1000)
    private String visualizeCustCol8;

    @Column(name = "VISUALIZE_CUST_COL9", length = 1000)
    private String visualizeCustCol9;

    @Column(name = "VISUALIZE_CUST_COL10", length = 1000)
    private String visualizeCustCol10;


    @Column(name = "FILTER_COLUMN", length = 4000)
    private String filterColumn;

    @Column(name = "DASHBORD_NAME", length = 100)
    private String dashboardName;

    @Column(name = "CHART_TITTLE", length = 200)
    private String chartTitle;

    @Column(name = "VISUALIZE_CUST_COL11", length = 3000)
    private String visualizeCustCol11;

    @Column(name = "VISUALIZE_CUST_COL12", length = 1000)
    private String visualizeCustCol12;

    @Column(name = "VISUALIZE_CUST_COL13", length = 1000)
    private String visualizeCustCol13;

    @Column(name = "VISUALIZE_CUST_COL14", length = 1000)
    private String visualizeCustCol14;

    @Column(name = "VISUALIZE_CUST_COL15", length = 1000)
    private String visualizeCustCol15;

    @Lob
    @Column(name = "VISUALIZE_CUST_COL16")
    private String visualizeCustCol16;

    @Lob
    @Column(name = "VISUALIZE_CUST_COL17")
    private String visualizeCustCol17;

    @Column(name = "VISUALIZE_CUST_COL18", length = 200)
    private String visualizeCustCol18;

    @Column(name = "DOMAIN", length = 100)
    private String domain;

    @Lob
    @Column(name = "VISUALIZE_CUST_COL19")
    private String visualizeCustCol19;

    @Lob
    @Column(name = "VISUALIZE_CUST_COL20")
    private String visualizeCustCol20;

    @Column(name = "VISUALIZE_CUST_COL21", length = 1000)
    private String visualizeCustCol21;

    @Column(name = "VISUALIZE_CUST_COL22", length = 1000)
    private String visualizeCustCol22;

    @Column(name = "VISUALIZE_CUST_COL23", length = 1000)
    private String visualizeCustCol23;

    @Column(name = "VISUALIZE_CUST_COL24", length = 1000)
    private String visualizeCustCol24;

    @Column(name = "Z_AXIS_VALUE", length = 1000)
    private String zAxisValue;


}
