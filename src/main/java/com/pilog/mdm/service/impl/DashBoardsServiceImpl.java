package com.pilog.mdm.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilog.mdm.model.ORecordVisualisation;
import com.pilog.mdm.repository.ChartDataRepository;
import com.pilog.mdm.repository.IDashBoardRepository;
import com.pilog.mdm.repository.IDynamicColumnDataRepository;
import com.pilog.mdm.repository.ORecordVisualisationRepository;
import com.pilog.mdm.requestdto.InputParams;
import com.pilog.mdm.service.IDashBoardsService;
import com.pilog.mdm.utils.PilogUtilities;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DashBoardsServiceImpl implements IDashBoardsService {

    @Autowired
    private ORecordVisualisationRepository orecordRepo;

    private final ChartDataRepository chartDataRepository;


    private final IDynamicColumnDataRepository columnData;
    @Value("${spring.datasource.driver-class-name}")
    private String dataBaseDriver;

    private final IDashBoardRepository dashBoardRepository;
    private static final Logger logger = LoggerFactory.getLogger(DashBoardsServiceImpl.class);

    public List<String> getChartCategoryNames() {
        return orecordRepo.findDashBoardNameByRoleId("MM_MANAGER");
    }

    public Map<String, Object> getChartCards(InputParams ip) {
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, String> dataobj = new HashMap<>();
        List<Map<String, String>> dataarr = new ArrayList<>();
        try {
            List<ORecordVisualisation> oRecordVisualisationList = orecordRepo.findByDashboardNameAndRoleIdOrderByChartSequenceNo(ip.getDashbordname(), "MM_MANAGER");
            if (oRecordVisualisationList != null && !oRecordVisualisationList.isEmpty()) {

                for (int i = 0; i < oRecordVisualisationList.size(); i++) {
                    ORecordVisualisation oRecordVisualisationObj = oRecordVisualisationList.get(i);
                    dataobj.put("xAxix", oRecordVisualisationObj.getXAxisValue());
                    dataobj.put("yAxix", oRecordVisualisationObj.getYAxisValue());
                    dataobj.put("type", oRecordVisualisationObj.getChartType());
                    dataobj.put("table", oRecordVisualisationObj.getTableName());
                    dataobj.put("chartid", oRecordVisualisationObj.getChartId());
                    dataobj.put("aggColumnName", oRecordVisualisationObj.getAggregateColumns());
                    dataobj.put("filterCondition", oRecordVisualisationObj.getFilterCondition());
                    dataobj.put("chartPropObj", oRecordVisualisationObj.getChartProperties());
                    dataobj.put("chartConfigObj", oRecordVisualisationObj.getChartConfigObject());
                    dataobj.put("labelLegend", oRecordVisualisationObj.getVisualizeCustCol10());
                    dataobj.put("Lebel", oRecordVisualisationObj.getChartTitle());
                    dataobj.put("colorsObj", oRecordVisualisationObj.getVisualizeCustCol8());
                    dataobj.put("chartConfigToggleStatus", oRecordVisualisationObj.getVisualizeCustCol9());
                    dataobj.put("compareChartsFlag", oRecordVisualisationObj.getVisualizeCustCol5());
                    dataobj.put("homeFilterColumn", oRecordVisualisationObj.getFilterColumn());
                    dataobj.put("fetchQuery", oRecordVisualisationObj.getVisualizeCustCol6());
                    dataobj.put("radioButtons", oRecordVisualisationObj.getVisualizeCustCol7());
                    dataobj.put("comboValue", oRecordVisualisationObj.getComboValue());
                    dataobj.put("currencyConversionStrObject", oRecordVisualisationObj.getVisualizeCustCol15());
                    logger.info("Processing chart data for DashboardName: {}", oRecordVisualisationObj.getDashboardName());
                    dataarr.add(dataobj);
                    logger.info("Fetching home card details for DashboardName: {}", oRecordVisualisationObj.getDashboardName());
                    if (oRecordVisualisationObj.getChartType().equalsIgnoreCase("card")) {
                        JSONObject cardData = (JSONObject) fetchHomeCardDetails(dataobj, ip);
                        if (cardData != null && !ObjectUtils.isEmpty(cardData)) {
                            result.put("CardData" + i, cardData);
                        }
                    } else {
                        logger.info("Getting chart data list for DashboardName: {}", oRecordVisualisationObj.getDashboardName());
                        JSONObject chartDataList = getChartDataList(dataobj, ip);
                        if (chartDataList != null && !ObjectUtils.isEmpty(chartDataList)) {
                            result.put("chartData" + i, chartDataList);
                        }
                    }

                    if (oRecordVisualisationObj.getFilterColumn() != null && !"".equals(oRecordVisualisationObj.getFilterColumn()) && !oRecordVisualisationObj.getChartType().equalsIgnoreCase("CARD") && !oRecordVisualisationObj.getChartType().equalsIgnoreCase("COMPARE_FILTER")) {
                        if (oRecordVisualisationObj.getFilterColumn() instanceof String) {
                            result.put("dropdowns", getDropdowns(oRecordVisualisationObj.getFilterColumn()));
                        }
                    }

                }
            }


        } catch (Exception e) {
            logger.error("An error occurred in getChartCards method.", e.getMessage());
        }
        return result;
    }


    @Transactional(rollbackFor = Exception.class)
    private Object fetchHomeCardDetails(Map<String, String> dataObj, InputParams ip) {
        JSONObject tabledataobj = new JSONObject();
        try {
            long count = 0;
            JSONArray result = new JSONArray();
            String selectQuery = "";
            String valueColumnName = "";
            String[] yAxisArray = null;
            String valueCSelect = "";
            String Lebel = "";
            String yAxisStringData = String.valueOf(dataObj.get("yAxix"));

            if (yAxisStringData != null && !yAxisStringData.isEmpty()) {
                JSONArray yAxisValueArray = (JSONArray) JSONValue.parse(yAxisStringData);
                if (yAxisValueArray != null && !yAxisValueArray.isEmpty()) {
                    for (Object obj : yAxisValueArray) {
                        JSONObject yAxisValueObject = (JSONObject) obj;
                        valueColumnName = ((String) yAxisValueObject.get("columnName")).replaceAll("\\([^\\.]*\\)", "");
                        valueCSelect = (String) yAxisValueObject.get("aggColumnName");
                        if ("UniqueCount".equalsIgnoreCase(valueCSelect)) {
                            valueCSelect = "Count";
                        }
                    }
                }
            }
            yAxisArray = valueColumnName.split("[.]", 0);
            String columnName = null;
            if (yAxisArray.length > 1) {
                columnName = yAxisArray[1].replace(")", "");
            }
            String tableName = String.valueOf(dataObj.get("table"));
            //	String columnName = ColumnStr.replace(")","");
            String type = valueCSelect.replaceAll("\\(.*", "");
//			if("UNIQUECOUNT".equals(type))
//		      type="COUNT";
            String selectedvalue = valueColumnName;
            String filterCondition = String.valueOf(dataObj.get("filterCondition"));
            Lebel = String.valueOf(dataObj.get("Lebel"));
            String chartType = String.valueOf(dataObj.get("type"));
            String Title = chartType.toUpperCase();
            String levelCondition = String.valueOf(dataObj.get("chartPropObj"));
            if ("null".equalsIgnoreCase(Lebel)) {
                JSONObject chartTitleObj = (JSONObject) JSONValue.parse(levelCondition);
                if (chartTitleObj != null) {
                    for (int i = 0; i < dataObj.size(); i++) {
                        Lebel = (String) chartTitleObj.get(Title + "CHARTTITLE" + i);
                        if (Lebel == null) {
                            Lebel = (String) chartTitleObj.get(Title + "CHARTTITLE");
                        }
                        if (Lebel != null && !"".equalsIgnoreCase(Lebel) && !"null".equalsIgnoreCase(Lebel)) {
                            break;
                        }
                    }
                }
            }

            if (Lebel == null) {
                Lebel = "Data N/A";
            }
            LocalDate now = LocalDate.now();
            LocalDate earlier = now.minusMonths(1);
            LocalDate earlierDay = earlier.minusDays(1);
            LocalDate earlierMonth = earlierDay.minusMonths(1);

            JSONObject paramFromObj = new JSONObject();
            paramFromObj.put("colName", columnName);
            paramFromObj.put("operator", "BETWEEN");
            paramFromObj.put("minvalue", earlierMonth);
            paramFromObj.put("maxvalue", earlierDay);
            JSONObject paramToObj = new JSONObject();
            paramToObj.put("colName", columnName);
            paramToObj.put("operator", "BETWEEN");
            paramToObj.put("minvalue", earlier);
            paramToObj.put("maxvalue", now);

            JSONArray filterColsArr = new JSONArray();
            JSONArray fromFilterArr = new JSONArray();
            JSONArray toFilterArr = new JSONArray();
            JSONObject chartConfigObj = new JSONObject();
            String whereCondQuery = "";
            String fromWhereCondQuery = "";
            String toWhereCondQuery = "";

            if (filterCondition != null && !"".equalsIgnoreCase(filterCondition) && !"null".equalsIgnoreCase(filterCondition) && !filterCondition.isEmpty()) {
                filterColsArr = (JSONArray) JSONValue.parse(filterCondition);
            }
            if (filterColsArr != null && !filterColsArr.isEmpty()) {
                for (int i = 0; i < filterColsArr.size(); i++) {
                    JSONObject filterColObj = (JSONObject) filterColsArr.get(i);
                    if (filterColObj != null && !filterColObj.isEmpty()) {
                        whereCondQuery += buildCondition(filterColObj);
                        if (i != filterColsArr.size() - 1) {
                            whereCondQuery += " AND ";
                        }
                    }
                }
            }
            if (fromFilterArr != null && !fromFilterArr.isEmpty()) {
                for (int i = 0; i < fromFilterArr.size(); i++) {
                    JSONObject fromFilterColObj = (JSONObject) fromFilterArr.get(i);
                    if (fromFilterColObj != null && !fromFilterColObj.isEmpty()) {
                        fromWhereCondQuery += buildCondition(fromFilterColObj);
                        if (i != fromFilterArr.size() - 1) {
                            fromWhereCondQuery += " AND ";
                        }
                    }
                }
            }
            if (toFilterArr != null && !toFilterArr.isEmpty()) {
                for (int i = 0; i < toFilterArr.size(); i++) {
                    JSONObject toFilterColObj = (JSONObject) toFilterArr.get(i);
                    if (toFilterColObj != null && !toFilterColObj.isEmpty()) {
                        toWhereCondQuery += buildCondition(toFilterColObj);
                        if (i != toFilterArr.size() - 1) {
                            toWhereCondQuery += " AND ";
                        }
                    }
                }
            }
            if (whereCondQuery != null && !"".equalsIgnoreCase(whereCondQuery) && !"null".equalsIgnoreCase(whereCondQuery)) {
                whereCondQuery = " WHERE " + whereCondQuery;
                if (fromWhereCondQuery != null && !"".equalsIgnoreCase(fromWhereCondQuery)) {
                    fromWhereCondQuery = whereCondQuery + " AND " + fromWhereCondQuery;
                }
                if (toWhereCondQuery != null && !"".equalsIgnoreCase(toWhereCondQuery)) {
                    toWhereCondQuery = whereCondQuery + " AND " + toWhereCondQuery;
                }
            } else {
                if (fromWhereCondQuery != null && !"".equalsIgnoreCase(fromWhereCondQuery)) {
                    fromWhereCondQuery = " WHERE " + fromWhereCondQuery;
                }
                if (toWhereCondQuery != null && !"".equalsIgnoreCase(toWhereCondQuery)) {
                    toWhereCondQuery = " WHERE " + toWhereCondQuery;
                }
            }
            if (tableName != null && !"".equalsIgnoreCase(tableName) && !"null".equalsIgnoreCase(tableName) && columnName != null && !"".equalsIgnoreCase(columnName) && !"null".equalsIgnoreCase(columnName)) {

                long fromCount = 0;
                long toCount = 0;
                String percent = "";
                if (fromWhereCondQuery != null && !"".equalsIgnoreCase(fromWhereCondQuery) && toWhereCondQuery != null && !"".equalsIgnoreCase(toWhereCondQuery)) {
                    if (fromWhereCondQuery != null && !"".equalsIgnoreCase(fromWhereCondQuery)) {
                        String fromQuery = "SELECT " + type + "(" + columnName + ") FROM " + tableName + fromWhereCondQuery;
                        System.out.println("line 238 " + fromQuery);
                        logger.info("Executing SQL Query: {}", fromQuery);

                        List<Object> resultList = dashBoardRepository.findResultList(fromQuery);
//						Query query = entityManager.createNativeQuery(fromQuery);
//						List<Object> fromCountList = query.getResultList();

                        if (resultList != null && !resultList.isEmpty()) {
                            fromCount = PilogUtilities.convertIntoInteger(resultList.get(0));
                        }
                    }
                    if (toWhereCondQuery != null && !"".equalsIgnoreCase(toWhereCondQuery)) {
                        String toQuery = "SELECT " + type + "(" + columnName + ") FROM " + tableName + toWhereCondQuery;
                        System.out.println("line 248 " + toQuery);
                        logger.info("Executing SQL Query: {}", toQuery);

                        List<Object> resultList = dashBoardRepository.findResultList(toQuery);
//						Query query = entityManager.createNativeQuery(toQuery);
//						List<Object> toCountList = query.getResultList();


                        if (resultList != null && !resultList.isEmpty()) {
                            toCount = PilogUtilities.convertIntoInteger(resultList.get(0));
                        }
                    }
                    if (fromCount > toCount) {
                        long diff = (fromCount - toCount);
                        double percentage = (diff * 100) / fromCount;
                        percent = percentage + "<img src='images/thumbsdown.png' class='icon' width='50px'>";
                    } else if (toCount > fromCount) {
                        long diff = (toCount - fromCount);
                        double percentage = (diff * 100) / toCount;
                        percent = percentage + "<img src='images/like_1.png' class='icon' width='50px'>";
                    }
                } else {
//					if("SALESDATA".equals(tableName))
//						tableName="SALES_DATA";
                    if (type != null && !"".equalsIgnoreCase(type)) {
                        selectQuery = "SELECT " + type + "(" + columnName + ") FROM " + tableName + whereCondQuery;
                    } else {
//	                    SELECT COUNT(*) AS VALUE FROM (SELECT  DISTINCT COMMODITY FROM  V_MAND_ATTR_VIEW )
                        selectQuery = "SELECT COUNT(*) AS VALUE FROM (SELECT  DISTINCT " + columnName + " FROM " + tableName + whereCondQuery + ")";
//	                   selectQuery = "SELECT  " + type + "(" + columnName + ") FROM " + tableName + whereCondQuery;
                    }
//					if (!("SALES_HAL7".equals(tableName)) && !("SALESDATA2".equals(tableName) ) && !("SALES_HAL27".equals(tableName))) {
                    System.out.println("line 277 " + selectQuery);
                    logger.info("Executing SQL Query: {}", selectQuery);

                    List<Object> resultList = dashBoardRepository.findResultList(selectQuery);
//						Query query = entityManager.createNativeQuery(selectQuery);
//					List<Object> countList = query.getResultList();


                    if (resultList != null && !resultList.isEmpty()) {
                        count = PilogUtilities.convertIntoInteger(resultList.get(0));
                    }
                    if (count >= 0) {
                        String datacount = withSuffix(count);
                        result.add(datacount);
                        result.add(columnName);
                        result.add(Lebel);
                    }
                    //		}
                }
                tabledataobj.put("result", result);
            }

        } catch (Exception e) {
            logger.error("An error occurred in fetchHomeCardDetails method.", e.getMessage());
        }
        return tabledataobj;
    }

    private String withSuffix(long count) {
        if (count < 1000) {
            return "" + count;
        }
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c", count / Math.pow(1000, exp), "KMGTPE".charAt(exp - 1));
    }


    @Transactional(rollbackFor = Exception.class)
    private JSONObject getChartDataList(Map<String, String> dataObj, InputParams ip) {
        JSONObject chartListObj = new JSONObject();
        JSONArray chartDataArr = new JSONArray();
        try {
            boolean flag = false;
            String selectQuery = "";
            String whereCondQuery = "";
            String groupByCond = "";

            // Extract data from dataObj
            String xAxis = String.valueOf(dataObj.get("xAxix"));
            String yAxis = String.valueOf(dataObj.get("yAxix"));
            String filterCondition = String.valueOf(dataObj.get("filterCondition"));
            String tables = String.valueOf(dataObj.get("table"));
            String chartType = String.valueOf(dataObj.get("type"));
            String chartTitleStr = "";
            String title = chartType.toUpperCase();
            String chartPropObj = String.valueOf(dataObj.get("chartPropObj"));
            JSONObject chartTitleObj = parseJSONObject(chartPropObj);

            if (chartTitleObj != null && !chartTitleObj.isEmpty()) {
                for (int i = 0; i < chartTitleObj.size(); i++) {
                    chartTitleStr = (String) chartTitleObj.get(title + "CHARTTITLE" + i);
                    if (chartTitleStr == null) {
                        chartTitleStr = (String) chartTitleObj.get(title + "CHARTTITLE");
                    }
                    if (isValidString(chartTitleStr)) {
                        break;
                    }
                }
            }

            if (chartTitleStr == null) {
                chartTitleStr = "No Data";
            }
            String orderBy = "";

            List<String> columnKeys = new ArrayList<>();
            JSONArray xAxisArr = parseJSONArray(xAxis);
            JSONArray yAxisArr = parseJSONArray(yAxis);
            JSONArray filterConditionArr = parseJSONArray(filterCondition);

            if (xAxis != null && !"".equalsIgnoreCase(xAxis) && !"null".equalsIgnoreCase(xAxis) && !xAxis.isEmpty()) {
                xAxisArr = (JSONArray) JSONValue.parse(xAxis);
            }
            if (yAxis != null && !"".equalsIgnoreCase(yAxis) && !"null".equalsIgnoreCase(yAxis) && !yAxis.isEmpty()) {
                yAxisArr = (JSONArray) JSONValue.parse(yAxis);
            }
            if (filterCondition != null && !"".equalsIgnoreCase(filterCondition) && !"null".equalsIgnoreCase(filterCondition)) {
                filterConditionArr = (JSONArray) JSONValue.parse(filterCondition);
            }

            if (xAxisArr != null && !xAxisArr.isEmpty()) {
                for (int i = 0; i < xAxisArr.size(); i++) {
                    JSONObject axisColObj = (JSONObject) xAxisArr.get(i);
                    if (axisColObj != null && !axisColObj.isEmpty()) {
                        String columnname = (String) axisColObj.get("columnName");
                        String regexPattern = "\\([^\\.]*\\)";
                        String columnName = columnname.replaceAll(regexPattern, "");

                        if (isValidString(columnName)) {
                            String[] columns = columnName.split(",");
                            if (columns != null && columns.length > 0) {
                                for (String column : columns) {
                                    String[] filteredColumnnameArr = column.split("\\.");
                                    if (filteredColumnnameArr.length > 1) {
                                        String filteredColumnname = filteredColumnnameArr[1].replaceAll("\\)", "");

                                        if (isValidString(filteredColumnname) && !"null".equalsIgnoreCase(filteredColumnname)) {
                                            filteredColumnname = filteredColumnname.replaceAll(" ", "_");
                                            columnKeys.add(filteredColumnname);
                                            selectQuery += " " + column + ", ";
                                            groupByCond += column + ", ";
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (yAxisArr != null && !yAxisArr.isEmpty()) {
                for (int i = 0; i < yAxisArr.size(); i++) {
                    JSONObject valueColObj = (JSONObject) yAxisArr.get(i);
                    if (valueColObj != null && !valueColObj.isEmpty()) {
                        String columnname = (String) valueColObj.get("columnName");
                        String regexPattern = "\\(.*?\\.";
                        String columnName = columnname.replaceAll(regexPattern, "(");

                        String aggColumnName = (String) valueColObj.get("aggColumnName");
                        String[] filteredColumnnameArr = columnName.split("\\.");
                        String filteredColumnname = filteredColumnnameArr[0];

                        if (isValidString(filteredColumnname)) {
                            filteredColumnname = filteredColumnname.replaceAll("_", " ");
                            columnKeys.add(filteredColumnname + "ASCOL" + i);

                            if (isValidString(aggColumnName)) {
                                if ("UniqueCount".equalsIgnoreCase(aggColumnName))
                                    columnName = columnName.replaceAll("(?i)UniqueCount", "Count");
                                selectQuery += " " + columnName + " AS COL" + i + " ,";
                                orderBy += (i == 0) ? " ORDER BY COL" + i + " DESC " : "";
                                flag = true;
                            } else {
                                selectQuery += " " + columnName + ", ";
                                orderBy += (i == 0) ? " ORDER BY " + columnName + " DESC " : "";
                                groupByCond += columnName + ",";
                            }
                        }
                    }
                }

                if (isValidChartType(chartType)) {
                    groupByCond = "";
                } else if (!flag || !isValidString(groupByCond)) {
                    groupByCond = "";
                } else {
                    groupByCond = PilogUtilities.trimChar(groupByCond, ',');
                    groupByCond = " GROUP BY " + groupByCond;
                }
            }

            if (filterConditionArr != null && !filterConditionArr.isEmpty()) {
                Iterator<Object> iterator = filterConditionArr.iterator();
                while (iterator.hasNext()) {
                    JSONObject filterColObj = (JSONObject) iterator.next();
                    if (filterColObj != null && !filterColObj.isEmpty()) {
                        whereCondQuery += buildCondition(filterColObj);
                        if (iterator.hasNext()) {
                            whereCondQuery += " AND ";
                        }
                    }
                }
            }

            if (whereCondQuery != null && !"".equalsIgnoreCase(whereCondQuery) && !"null".equalsIgnoreCase(whereCondQuery)) {
                whereCondQuery = " WHERE " + whereCondQuery;
            }

            if (isValidString(selectQuery) && tables != null && !tables.isEmpty()) {
                //			String tableName = tables.equals("SALESDATA") ? "SALES_DATA" : tables;
                String countQuery = "";

                selectQuery = PilogUtilities.trimChar(selectQuery, ',');
                selectQuery = "SELECT " + selectQuery + " FROM " + tables + whereCondQuery + groupByCond + orderBy;

                countQuery = "SELECT COUNT(*) FROM " + tables + whereCondQuery + groupByCond;
                System.out.println("selectQuery ::: " + selectQuery);
            }
            //		if (!("SALES_HAL7".equals(tables)) && !("SALESDATA2".equals(tables)) && !("SALES_HAL27".equals(tables))) {
            System.out.println("line 461 " + selectQuery);

            try {
                logger.info("Executing SQL Query: {}", selectQuery);
                List<Object> first10ResultList = dashBoardRepository.findFirst10ResultList(selectQuery);
//					Query query = entityManager.createNativeQuery(selectQuery);
//					query.setMaxResults(10);
//
//					// Execute the query and retrieve data
//					List<Object> selectData = query.getResultList();

                // Process the data
                for (Object data : first10ResultList) {
                    if (data instanceof Object[]) {
                        Object[] chartData = (Object[]) data;
                        String chartDataX = null;
                        if (chartData[0] != null) {
                            if (chartData[0] instanceof String) {
                                chartDataX = (String) chartData[0];
                            } else if (chartData[0] instanceof java.sql.Timestamp) {
                                Timestamp chartDatum = (Timestamp) chartData[0];
                                chartDataX = chartDatum.toString();
                            } else {
                                System.out.println("Unsupported type for chartDataX: " + chartData[0].getClass());
                            }
                        } else {
                            System.out.println("chartData[0] is null.");
                        }

                        BigDecimal chartDataY = convertToBigDecimal(chartData[1]);
                        int chartSize = chartData.length;
                        JSONObject chartDataObj = new JSONObject();

                        // Construct JSON object
                        if (chartDataX != null) {
                            chartDataObj.put("X", chartDataX);
                        } else {
                            chartDataObj.put("X", "0");
                        }

                        if (chartDataY != null) {
                            chartDataObj.put("Y", chartDataY);
                        } else {
                            chartDataObj.put("Y", 0);
                        }

                        if (chartSize == 3) {
                            chartDataObj.put("Z", (BigDecimal) chartData[2]);
                        }

                        // Add the JSON object to the array
                        chartDataArr.add(chartDataObj);
                    } else {
                        // Handle invalid data structure
                        System.out.println("Invalid data structure: " + data);
                    }
                    if (!"Card".equals(chartType)) {
                        chartListObj.put("chartTitle", chartTitleStr);
                        chartListObj.put("chartType", chartType);
                        chartListObj.put("chartLevelsAndValueObj", chartDataArr);
                    }

                }
            } catch (Exception e) {
                // Handle exceptions appropriately
                logger.error("An error occurred in getChartDataList method inside if block .", e.getMessage());
            }

//		}
            System.out.println("selectQuery2 :::" + selectQuery);
        } catch (Exception e) {
            logger.error("An error occurred in getChartDataList method.", e.getMessage());
        }
        return chartListObj;
    }

    @Transactional
    private String buildCondition(JSONObject paramObj) {
        String conditionQuery = "";
        try {
            String operatorName = (String) paramObj.get("operator");
            String value = (String) paramObj.get("values");
            String columnName = (String) paramObj.get("colName");
            String tableName = (String) paramObj.get("tableName");

            if (columnName != null && columnName.endsWith("DATE")) {
                // value = "TO_DATE('" + value + "', 'MM/DD/YYYY')";
                // value = value.substring(0, value.indexOf("GMT") - 9).trim();
                if (dataBaseDriver != null && !"".equalsIgnoreCase(dataBaseDriver)) {
                    if (dataBaseDriver.toUpperCase().contains("ORACLE")) {
                        columnName = "TO_DATE(TO_CHAR(" + columnName + ",'DD-MM-YYYY'), 'DD-MM-YYYY')";
                        value = "TO_DATE('" + value + "','DD-MM-YYYY')";
                        if ("BETWEEN".equalsIgnoreCase(operatorName)) {
                            String minValue = (String) paramObj.get("minvalue");
                            String maxvalue = (String) paramObj.get("maxvalue");
                            if (!(minValue != null && !"".equalsIgnoreCase(minValue))) {
                                minValue = "01-01-1947";
                            }
                            if (!(maxvalue != null && !"".equalsIgnoreCase(maxvalue))) {
                                Date date = new Date();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                maxvalue = formatter.format(date);
                            }
                            value = "TO_DATE('" + minValue + "','DD-MM-YYYY') AND TO_DATE('" + maxvalue + "','DD-MM-YYYY')";
                        }
                    } else if (dataBaseDriver.toUpperCase().contains("MYSQL")) {
                        columnName = "STR_TO_DATE(DATE_FORMAT(" + columnName + ",'DD-MM-YYYY'), 'DD-MM-YYYY')";
                        value = "STR_TO_DATE('" + value + "','DD-MM-YYYY')";
                        if ("BETWEEN".equalsIgnoreCase(operatorName)) {
                            String minValue = (String) paramObj.get("minvalue");
                            String maxvalue = (String) paramObj.get("maxvalue");
                            if (!(minValue != null && !"".equalsIgnoreCase(minValue))) {
                                minValue = "01-01-1947";
                            }
                            if (!(maxvalue != null && !"".equalsIgnoreCase(maxvalue))) {
                                Date date = new Date();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                maxvalue = formatter.format(date);
                            }
                            value = "STR_TO_DATE('" + minValue + "','DD-MM-YYYY') AND STR_TO_DATE('" + maxvalue + "','DD-MM-YYYY')";
                        }
                    } else if (dataBaseDriver.toUpperCase().contains("SQLSERVER")) {
                        columnName = "CONVERT(CONVERT(VARCHAR(10)," + columnName + ",'DD-MM-YYYY'), 'DD-MM-YYYY')";
                        value = "CONVERT('" + value + "','DD-MM-YYYY')";
                        if ("BETWEEN".equalsIgnoreCase(operatorName)) {
                            String minValue = (String) paramObj.get("minvalue");
                            String maxvalue = (String) paramObj.get("maxvalue");
                            if (!(minValue != null && !"".equalsIgnoreCase(minValue))) {
                                minValue = "01-01-1947";
                            }
                            if (!(maxvalue != null && !"".equalsIgnoreCase(maxvalue))) {
                                Date date = new Date();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                maxvalue = formatter.format(date);
                            }
                            value = "CONVERT('" + minValue + "','DD-MM-YYYY') AND CONVERT('" + maxvalue + "','DD-MM-YYYY')";
                        }
                    } else if (dataBaseDriver.toUpperCase().contains("DB2")) {

                    }

                }

            }
            if (operatorName != null && !"".equalsIgnoreCase(operatorName) && value != null && !"".equalsIgnoreCase(value)) {
                operatorName = operatorName.toUpperCase();
                switch (operatorName) {
                    case "CONTAINING":
                        conditionQuery = "UPPER(" + columnName + ") LIKE '%" + value + "%'";
                        break;
                    case "EQUALS":
                        if (columnName.contains("_DATE")) {
                            conditionQuery = " " + columnName + " = " + value + "";
                        } else {
                            conditionQuery = " " + columnName + " = '" + value + "'";
                        }
                        break;
                    case "NOT EQUALS":
                        if (columnName.contains("_DATE")) {

                            conditionQuery = " " + columnName + " != " + value + "";
                        } else {
                            conditionQuery = " " + columnName + " != '" + value + "'";
                        }
                        break;

                    case "GREATER THAN":
                        if (columnName.contains("_DATE")) {

                            conditionQuery = " " + columnName + " > " + value + "";

                        } else {
                            conditionQuery = " " + columnName + " > '" + value + "'";
                        }
                        break;
                    case "LESS THAN":
                        if (columnName.contains("_DATE")) {
                            conditionQuery = " " + columnName + " < " + value + "";
                        } else {
                            conditionQuery = " " + columnName + " < '" + value + "'";
                        }
                        break;

                    case "BEGINING WITH":
                        conditionQuery = " " + columnName + " LIKE '" + value + "%'";

                        break;
                    case "ENDING WITH":
                        conditionQuery = " " + columnName + " LIKE '%" + value + "'";
                        break;
                    case "LIKE":
                        conditionQuery = " " + columnName + " LIKE '" + value + "'";
                        break;
                    case "NOT LIKE":
                        conditionQuery = " " + columnName + " NOT LIKE '" + value + "'";
                        break;
                    case "IS":
                        conditionQuery = " " + columnName + " IS  NULL";
                        break;
                    case "IS NOT":
                        conditionQuery = " " + columnName + " IS NOT NULL";
                        break;
                    case ">":
                        conditionQuery = " " + columnName + " > '" + value + "'";
                        break;
                    case "<":
                        conditionQuery = " " + columnName + " < '" + value + "'";
                        break;
                    case ">=":
                        conditionQuery = " " + columnName + " >= " + value + "";
                        break;
                    case "<=":
                        conditionQuery = " " + columnName + " <= " + value + "";
                        break;
                    case "IN":

                        conditionQuery = " " + columnName + " IN " + generateInStr(value) + "";
                        break;
                    case "NOT IN":
                        conditionQuery = " " + columnName + " NOT IN " + generateInStr(value) + "";
                        break;
                    case "BETWEEN":
                        conditionQuery = " " + columnName + " BETWEEN " + value;
                        break;
                    default:
                        conditionQuery = " " + columnName + " " + operatorName + " " + value;
                }

            }

            // query = query + " AND " + getCondition(filterdatafield, filtercondition,
            // filtervalue);
        } catch (Exception e) {
            logger.error("An error occurred in buildCondition method.", e.getMessage());
        }
        return conditionQuery;
    }

    private String generateInStr(String value) {

        try {
            System.err.println("value:::Before:::" + value);
            if (value != null && !"".equalsIgnoreCase(value)) {
                value = PilogUtilities.trimChar(value, ',');
                if (value != null && value.contains(",") && value.contains("','")) {
                    value = "(" + value + ")";
                } else if (value.contains(",")) {
                    value = value.trim();
                    System.out.println("value is :::" + value);
                    value = "('" + value.replaceAll(",", "','") + "')";
                    // conditionStr = columnName + " NOT IN ('" + convertedValue.replaceAll(",",
                    // "','") + "')";
                } else {
                    value = "('" + value + "')";
                    // conditionStr = columnName + " NOT IN ('" + convertedValue + "')";
                }
            } else {
                value = "('')";
            }
            System.err.println("value:::After:::" + value);

        } catch (Exception e) {
            logger.error("An error occurred in generateInStr method.", e);
        }
        return value;
    }

    private static BigDecimal convertToBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof String && isNumeric((String) value)) {
            return new BigDecimal((String) value);
        } else {
            System.out.println("Not a valid number: " + value);
            return null; // or handle this case according to your requirements
        }
    }

    // Helper method to check if a string is numeric
    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private boolean isValidString(String str) {
        return str != null && !"".equalsIgnoreCase(str) && !"null".equalsIgnoreCase(str);
    }

    private boolean isValidChartType(String chartType) {
        return chartType != null && ("indicator".equalsIgnoreCase(chartType) || "Card".equalsIgnoreCase(chartType));
    }

    private JSONArray parseJSONArray(String jsonString) {
        if (jsonString != null && !"".equalsIgnoreCase(jsonString) && !"null".equalsIgnoreCase(jsonString)) {
            return (JSONArray) JSONValue.parse(jsonString);
        }
        return null;
    }

    private JSONObject parseJSONObject(String jsonString) {
        if (isValidString(jsonString)) {
            return (JSONObject) JSONValue.parse(jsonString);
        }
        return null;
    }

    private Map<String, Set<String>> getDropdowns(String filterColumn) {
        Map<String, Set<String>> resultMap = new HashMap();
        try {
            Set<String> resultColumns = new HashSet<>();
            Set<String> resultTable = new HashSet<>();

            String[] dropdowns = filterColumn.split(",");
            for (String dropdown : dropdowns) {
                String tableName = dropdown.substring(0, dropdown.lastIndexOf("."));
                String columnNames = dropdown.substring(dropdown.lastIndexOf(".") + 1).replaceAll("_", " ");
                resultColumns.add(columnNames);
                resultTable.add(tableName);
            }
            resultMap.put("TABLE_NAME", resultTable);
            resultMap.put("COLUMN_NAMES", resultColumns);
        } catch (Exception e) {
            e.getMessage();
        }
        return resultMap;
    }

    @Override
    public Set<String> getColumnData(String columnName, String tableName) {
        return columnData.getColumnData(columnName, tableName);
    }

    //omer_sabir
    @Override
    public Map<String, Object> getChartDataAndCardData(InputParams ip) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            List<ORecordVisualisation> oRecordVisualisationList = orecordRepo.findByDashboardNameAndRoleIdOrderByChartSequenceNo(ip.getDashbordname(), "MM_MANAGER");
            if (oRecordVisualisationList != null && !oRecordVisualisationList.isEmpty()) {

                for (int i = 0; i < oRecordVisualisationList.size(); i++) {
                    ORecordVisualisation oRecordVisualisationObj = oRecordVisualisationList.get(i);
                    logger.info("Processing chart and card data for DashboardName: {}", oRecordVisualisationObj.getDashboardName());
                    logger.info("Fetching home card details for DashboardName: {}", oRecordVisualisationObj.getDashboardName());
                    if (oRecordVisualisationObj.getChartType().equalsIgnoreCase("card")) {
                        Map<String, Object> cardData = getCardData(oRecordVisualisationObj, ip);
                        if (cardData != null && !cardData.isEmpty()) {
                            result.put("CardData" + i, cardData);
                        }
                    } else {
                        logger.info("Getting chart data list for DashboardName: {}", oRecordVisualisationObj.getDashboardName());
                        Map<String, Object> chartData = getChartData(oRecordVisualisationObj, ip);
                        if (chartData != null && !chartData.isEmpty()) {
                            result.put("chartData" + i, chartData);
                        }
                    }

                    if (oRecordVisualisationObj.getFilterColumn() != null && !"".equals(oRecordVisualisationObj.getFilterColumn()) && !oRecordVisualisationObj.getChartType().equalsIgnoreCase("CARD") && !oRecordVisualisationObj.getChartType().equalsIgnoreCase("COMPARE_FILTER")) {
                        result.put("dropdowns", getDropdowns(oRecordVisualisationObj.getFilterColumn()));
                    }

                }
            }
        } catch (Exception e) {
            logger.error("An error occurred in getChartCards method.", e.getMessage());
        }
        return result;
    }


    private Map<String, Object> getChartData(ORecordVisualisation oRecordVisualisationObj, InputParams input) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> xAxisMap = new ArrayList<>();
        List<Map<String, String>> yAxisMap = new ArrayList<>();
        List<Map<String, String>> filterConditionMap = new ArrayList<>();
        List<Map<String, Object>> chartCoordinates = new ArrayList<>();
        List<Map<String, Object>> chartCoordinatesByQuery = new ArrayList<>();
        Map<String, String> chartPropertiesMap = new HashMap<>();
        String xAxisValue = oRecordVisualisationObj.getXAxisValue();
        String yAxisValue = oRecordVisualisationObj.getYAxisValue();
        String filterCondition = oRecordVisualisationObj.getFilterCondition();
        String chartType = oRecordVisualisationObj.getChartType();
        String chartProperties = oRecordVisualisationObj.getChartProperties();
        String dbQuery = oRecordVisualisationObj.getDbQuery();
        String stacked="";

        if ((xAxisValue == null || xAxisValue.isEmpty()) && (yAxisValue == null || yAxisValue.isEmpty())) {
            if (dbQuery == null || dbQuery.isEmpty()) {
                return result;
            } else {
                if (dbQuery != null && !dbQuery.isEmpty()) {
                    chartCoordinatesByQuery = chartCoordinatesByQuery(dbQuery);

                }

            }
        }
        if (xAxisValue != null && !xAxisValue.isEmpty() && !"[]".equals(xAxisValue)) {
            xAxisMap = convertStringToListMap(xAxisValue);
        }
        if (yAxisValue != null && !yAxisValue.isEmpty() && !"[]".equals(yAxisValue)) {
            yAxisMap = convertStringToListMap(yAxisValue);
        }
        if (filterCondition != null && !filterCondition.isEmpty() && !"[]".equals(filterCondition)) {
            filterConditionMap = convertStringToListMap(filterCondition);
        }
        if (chartProperties != null && !chartProperties.isEmpty() ) {
            chartPropertiesMap = convertJsonStrToMap(chartProperties);
        }

        chartCoordinates = chartCoordinates(xAxisMap, yAxisMap, filterConditionMap, input);
        if (chartCoordinatesByQuery != null && !chartCoordinatesByQuery.isEmpty()) {
            chartCoordinates.addAll(chartCoordinatesByQuery);
            if(hasMapWithMoreThanTwoKeys(chartCoordinatesByQuery)){
                if("bar".equals(chartType) ||"column".equals(chartType) )
                    stacked="stacked";
            }
        }

        if (chartCoordinates != null && !chartCoordinates.isEmpty()) {
            result.put("chartCoordinates", chartCoordinates);
            result.put("chartType", stacked+chartType);
            result.put("chartTitle", chartPropertiesMap.get(chartType.toUpperCase() + "CHARTTITLE") == null ? "" : chartPropertiesMap.get(chartType.toUpperCase() + "CHARTTITLE"));
        }
        return result;
    }

    private Map<String, Object> getCardData(ORecordVisualisation oRecordVisualisationObj, InputParams input) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> yAxisMap = new ArrayList<>();
        List<Map<String, String>> filterConditionMap = new ArrayList<>();
        List<Map<String, Object>> cardData = new ArrayList<>();
        String yAxisValue = oRecordVisualisationObj.getYAxisValue();
        String filterCondition = oRecordVisualisationObj.getFilterCondition();
        String chartType = oRecordVisualisationObj.getChartType();

        if (yAxisValue == null || yAxisValue.isEmpty()) {
            return result;
        } else {
            yAxisMap = convertStringToListMap(yAxisValue);
        }
        if (filterCondition != null && !filterCondition.isEmpty()) {
            filterConditionMap = convertStringToListMap(filterCondition);
        }

        cardData = cardCoordinates(yAxisMap, filterConditionMap, input.getConditions());
        if (cardData != null && !cardData.isEmpty()) {
            result.put("cardData", cardData);
            result.put("chartType", chartType);
            result.put("cardTitle", oRecordVisualisationObj.getChartTitle());
        }
        return result;
    }

    private List<Map<String, String>> convertStringToListMap(String jsonStringList) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, String>> strToListMap = new ArrayList<>();
        try {
            strToListMap = objectMapper.readValue(jsonStringList, new TypeReference<List<Map<String, String>>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return strToListMap;
    }

    public Map<String, String> convertJsonStrToMap(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> strToMap = new HashMap<>();
        try {
            strToMap = objectMapper.readValue(jsonString, new TypeReference<Map<String, String>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return strToMap;
    }


    private List<Map<String, Object>> chartCoordinates(List<Map<String, String>> xAxisMap, List<Map<String, String>> yAxisMap, List<Map<String, String>> filterConditionMap, InputParams input) {
        List<Map<String, Object>> resultMap = new ArrayList<>();
        if ((xAxisMap == null || xAxisMap.isEmpty()) && (yAxisMap == null || yAxisMap.isEmpty())) {
            return resultMap;
        }
        Map<String, List<String>> conditions = input.getConditions();
        String conditionTable = input.getConditionTable();
        // X-AXIS
        String yAxisTableName = "";
        String xAxisColumnName = "";

        // Y_AXIS
        List<String> aggFunctionNames = new ArrayList<>();
        List<String> yAxisColumnNames = new ArrayList<>();

        // DB Filter Conditions
        String colName = "";
        String operator = "";
        String values = "";

        // Select Query
        StringBuilder queryBuilder = new StringBuilder("SELECT ");

        if (xAxisMap != null && !xAxisMap.isEmpty()) {
            for (Map<String, String> xAxis : xAxisMap) {
                xAxisColumnName = xAxis.get("columnName");
                queryBuilder.append(xAxisColumnName).append(" AS X").append(xAxisMap.indexOf(xAxis) + 1).append(", ");
            }
            queryBuilder.setLength(queryBuilder.length() - 2);
        }

        if (yAxisMap != null && !yAxisMap.isEmpty()) {
            for (Map<String, String> yAxis : yAxisMap) {
                aggFunctionNames.add(yAxis.get("aggColumnName"));
                String yAxisColumnName = yAxis.get("columnName");
                yAxisTableName = yAxis.get("tableName");
                if (yAxisColumnName != null)
                     if (xAxisMap != null && !xAxisMap.isEmpty()) {
                         queryBuilder.append(", ").append(yAxisColumnName).append(" AS Y").append(yAxisMap.indexOf(yAxis) + 1);
                     }else {
                         queryBuilder.append(yAxisColumnName).append(" AS Y").append(yAxisMap.indexOf(yAxis) + 1);
                     }
                yAxisColumnNames.add(yAxisColumnName);
            }
        }

        queryBuilder.append(" FROM ").append(yAxisTableName);

        if (filterConditionMap != null && !filterConditionMap.isEmpty()) {
            queryBuilder.append(" WHERE ");
            for (Map<String, String> filterCondition : filterConditionMap) {
                colName = filterCondition.get("colName");
                operator = filterCondition.get("operator");
                values = filterCondition.get("values");

                String[] valueArr = values.split(",");
                List<String> valueArray = Arrays.stream(valueArr)
                        .map(val -> {
                            if (val.contains("$")) {
                                return val.replace("$", ",");
                            } else if (val.contains("#")) {
                                return val.replace("#", " ");
                            }
                            return val;
                        })
                        .collect(Collectors.toList());
                StringBuilder valuesStringBuilder = new StringBuilder();

                for (String val : valueArray) {
                    valuesStringBuilder.append("'").append(val.trim()).append("', ");
                }
                if (valuesStringBuilder.length() > 0) {
                    valuesStringBuilder.setLength(valuesStringBuilder.length() - 2);
                    queryBuilder.append(colName).append(" ").append(operator).append(" (").append(valuesStringBuilder.toString()).append(") ");
                }
            }
        }

        // Where Condition Start

        if ((yAxisTableName).equalsIgnoreCase(conditionTable)) {
            if (conditions != null && !conditions.isEmpty()) {
                Set<String> keys = conditions.keySet();
                if (!queryBuilder.toString().contains("WHERE")) {
                    queryBuilder.append(" WHERE ");
                } else {
                    queryBuilder.append(" AND ");
                }
                for (String key : keys) {
                    String colNamee = key;
                    queryBuilder.append(colNamee);

                    List<String> valuess = conditions.get(key);
                    StringBuilder valuesStringBuilder = new StringBuilder();

                    for (String val : valuess) {
                        valuesStringBuilder.append("'").append(val.trim()).append("', ");
                    }

                    if (valuesStringBuilder.length() > 0) {
                        valuesStringBuilder.setLength(valuesStringBuilder.length() - 2);
                        queryBuilder.append(" IN (").append(valuesStringBuilder.toString()).append(") AND ");
                    }
                }
                queryBuilder.setLength(queryBuilder.length() - 5);

            }
        }

        if (xAxisMap != null && !xAxisMap.isEmpty()) {
            queryBuilder.append(!queryBuilder.toString().contains("WHERE") ? " WHERE " : " AND ")
                    .append(xAxisColumnName).append(" IS NOT NULL ");

            queryBuilder.append(" GROUP BY ").append(xAxisColumnName).append(", ");
            queryBuilder.setLength(queryBuilder.length() - 2);
        }


        // Where Condition end


        if (!yAxisMap.isEmpty()) {
                 queryBuilder.append(" HAVING ");

            for (String yAxis : yAxisColumnNames) {
                queryBuilder.append(yAxis).append(" IS NOT NULL AND ");
            }

            queryBuilder.setLength(queryBuilder.length() - 5);
        }

        queryBuilder.append(" ORDER BY ").append(yAxisColumnNames.get(0)).append(" DESC");

        String query = queryBuilder.toString();
        List<Map<String, Object>> chartDataList = chartDataRepository.getChartData(query);

        int limit = Math.min(10, chartDataList.size());
        for (int i = 0; i < limit; i++) {
            resultMap.add(chartDataList.get(i));
        }
        return resultMap;
    }

    private List<Map<String, Object>> chartCoordinatesByQuery(String dbQuery) {
        List<Map<String, Object>> resultMap = new ArrayList<>();
        List<Map<String, Object>> chartData = chartDataRepository.getChartData(dbQuery);

        int limit = Math.min(10, chartData.size());
        for (int i = 0; i < limit; i++) {
            resultMap.add(chartData.get(i));
        }
        return resultMap;

    }

    private List<Map<String, Object>> cardCoordinates(List<Map<String, String>> yAxisMap, List<Map<String, String>> filterConditionMap, Map<String, List<String>> conditions) {
        List<Map<String, Object>> resultMap = new ArrayList<>();


        String yAxisTableName = "";
        // Y_AXIS
        List<String> aggFunctionNames = new ArrayList<>();
        List<String> yAxisColumnNames = new ArrayList<>();

        // DB Filter Conditions
        String colName = "";
        String operator = "";
        String values = "";

        // Select Query
        StringBuilder queryBuilder = new StringBuilder("SELECT ");

        if (yAxisMap != null && !yAxisMap.isEmpty()) {
            for (Map<String, String> yAxis : yAxisMap) {
                aggFunctionNames.add(yAxis.get("aggColumnName"));
                String yAxisColumnName = yAxis.get("columnName");
                yAxisTableName = yAxis.get("tableName");
                if (yAxisColumnName != null)
                    queryBuilder.append(yAxisColumnName).append(" AS Y");
                yAxisColumnNames.add(yAxisColumnName);
            }
        }

        queryBuilder.append(" FROM ").append(yAxisTableName);

        if (filterConditionMap != null && !filterConditionMap.isEmpty()) {
            queryBuilder.append(" WHERE ");
            for (Map<String, String> filterCondition : filterConditionMap) {
                colName = filterCondition.get("colName");
                operator = filterCondition.get("operator");
                values = filterCondition.get("values");

                String[] valueArray = values.split(",");
                StringBuilder valuesStringBuilder = new StringBuilder();

                for (String val : valueArray) {
                    valuesStringBuilder.append("'").append(val.trim()).append("', ");
                }
                if (valuesStringBuilder.length() > 0) {
                    valuesStringBuilder.setLength(valuesStringBuilder.length() - 2);
                    queryBuilder.append(colName).append(" ").append(operator).append(" (").append(valuesStringBuilder.toString()).append(") ");
                }
            }
        }
        if (conditions != null && !conditions.isEmpty()) {
            Set<String> keys = conditions.keySet();
            if (!queryBuilder.toString().contains("WHERE")) {
                queryBuilder.append(" WHERE ");
            } else {
                queryBuilder.append(" AND ");
            }

            for (String key : keys) {
                String colNamee = key;
                queryBuilder.append(colNamee);

                List<String> valuess = conditions.get(key);
                StringBuilder valuesStringBuilder = new StringBuilder();

                for (String val : valuess) {
                    valuesStringBuilder.append("'").append(val.trim()).append("', ");
                }

                if (valuesStringBuilder.length() > 0) {
                    valuesStringBuilder.setLength(valuesStringBuilder.length() - 2);
                    queryBuilder.append(" IN (").append(valuesStringBuilder.toString()).append(") AND ");
                }
            }
            queryBuilder.setLength(queryBuilder.length() - 5);

        }

//        queryBuilder.append(" ORDER BY ").append(yAxisColumnNames.get(0)).append(" DESC");

        String query = queryBuilder.toString();
        List<Map<String, Object>> cardtDataList = chartDataRepository.getChartData(query);
        int limit = Math.min(10, cardtDataList.size());
        for (int i = 0; i < limit; i++) {
            resultMap.add(cardtDataList.get(i));
        }
        return resultMap;
    }
    private  boolean hasMapWithMoreThanTwoKeys(List<Map<String, Object>> list) {
        for (Map<String, Object> map : list) {
            if (map.size() > 2) {
                return true;
            }
        }
        return false;
    }
    @Override
    public Map<String,List<String>> getHomePageFilterData(String chartType) {
        Map<String,List<String>> tableWithColumns=new LinkedHashMap<>();
        List<String> filterStrList=new ArrayList<>();
        List<String> tableList = chartDataRepository.getHomePageFilterData(chartType);
        for(String tableName:tableList) {
            List<String> homePageFilterTableColumns = chartDataRepository.getHomePageFilterTableColumns(tableName);
            tableWithColumns.put(tableName,homePageFilterTableColumns);
        }
        String filterColumnStr = chartDataRepository.getHomePageFilterColumn(chartType);
        if(filterColumnStr!=null){
            if(filterColumnStr.contains(",")){
                String[] filterStrArr = filterColumnStr.split(",");
                for(String filterStr:filterStrArr){
                    filterStrList.add(filterStr);
                }
            }else {
                filterStrList.add(filterColumnStr);
            }
        }
        tableWithColumns.put("FilterColumns",filterStrList);
        return tableWithColumns;
    }

    @Override
    public String getHomePageFilterDataSave(Map<String,List<String>> inputs) {
       StringBuffer columns=new StringBuffer();
        inputs.forEach((k,v)->{
            if(!k.equalsIgnoreCase("dashboardName")) {
                for (String column : v) {
                    columns.append(k).append(".").append(column).append(",");
                }
            }
        });
        if (columns.length() > 0) {
            columns.deleteCharAt(columns.length() - 1);
        }
        String dashboardName = inputs.get("dashboardName").get(0);
        if (dashboardName != null && !dashboardName.isEmpty()) {
            orecordRepo.updateFilterColumn(columns.toString(), "FILTER", dashboardName);
        }
      return "Filter Column Updated Successfully";
    }


}
