package com.pilog.mdm.service;

import com.pilog.mdm.model.ORecordVisualisation;
import com.pilog.mdm.repository.IDashBoardRepository;
import com.pilog.mdm.repository.ORecordVisualisationRepository;
import com.pilog.mdm.requestdto.InputParams;
import com.pilog.mdm.utils.PilogUtilities;
import lombok.RequiredArgsConstructor;
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


@Service
@RequiredArgsConstructor
public class DashBoardsService {

	@Autowired
	public ORecordVisualisationRepository orecordRepo;
	@Value("${spring.datasource.driver-class-name}")
	private String dataBaseDriver;

	private final IDashBoardRepository dashBoardRepository;
	private static final Logger logger = LoggerFactory.getLogger(DashBoardsService.class);
	public List<String> getChartCategoryNames( ) {
		List<String> chartCategoryNames = orecordRepo.findDashBoardNameByRoleId("MM_MANAGER");
		return chartCategoryNames;
	}


	public Map<String,Object> getChartCards(InputParams ip) {
		Map<String,Object> result=new HashMap<>();
		Map<String,String> dataobj=new HashMap<>();
		List<Map<String,String>> dataarr=new ArrayList<>();
		try {
		List<ORecordVisualisation> oRecordVisualisationList = orecordRepo.findByDashboardNameAndRoleIdOrderByChartSequenceNo(ip.getDashbordname(), "MM_MANAGER");
			if (oRecordVisualisationList != null && !oRecordVisualisationList.isEmpty()) {

				for (int i = 0; i < oRecordVisualisationList.size(); i++) {
					ORecordVisualisation oRecordVisualisationObj = oRecordVisualisationList.get(i);
					if(oRecordVisualisationObj.getChartProperties()!=null | oRecordVisualisationObj.getAggregateColumns()!=null){
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
					dataobj.put("homeFilterColumn",oRecordVisualisationObj.getFilterColumn());
					dataobj.put("fetchQuery", oRecordVisualisationObj.getVisualizeCustCol6());
					dataobj.put("radioButtons", oRecordVisualisationObj.getVisualizeCustCol7());
					dataobj.put("comboValue", oRecordVisualisationObj.getComboValue());
					dataobj.put("currencyConversionStrObject",oRecordVisualisationObj.getVisualizeCustCol15());
					logger.info("Processing chart data for DashboardName: {}", oRecordVisualisationObj.getDashboardName());
					dataarr.add(dataobj);
					logger.info("Fetching home card details for DashboardName: {}", oRecordVisualisationObj.getDashboardName());
					result.put("CardData" + i + "", fetchHomeCardDetails( dataobj, ip));
					logger.info("Getting chart data list for DashboardName: {}", oRecordVisualisationObj.getDashboardName());
				result.put("chartData" + i + "", getChartDataList(dataobj, ip));
				}}
			}

		} catch (Exception e) {
			logger.error("An error occurred in getChartCards method.", e.getMessage());
		}
		return result;
	}
	@Transactional(rollbackFor = Exception.class)
	private Object fetchHomeCardDetails(Map<String,String> dataObj, InputParams ip) {
		JSONObject tabledataobj = new JSONObject();
		try {
			long count = 0;
			JSONArray result = new JSONArray();
			String selectQuery = "";
			String valueColumnName = "";
			String[] yAxisArray = null;
			String valueCSelect = "";
			String Lebel ="";
			String yAxisStringData = String.valueOf(dataObj.get("yAxix"));

			if (yAxisStringData != null && !yAxisStringData.isEmpty()) {
				JSONArray yAxisValueArray = (JSONArray) JSONValue.parse(yAxisStringData);
				if (yAxisValueArray != null && !yAxisValueArray.isEmpty()) {
					for (Object obj : yAxisValueArray) {
						JSONObject yAxisValueObject = (JSONObject) obj;
						 valueColumnName = ((String) yAxisValueObject.get("columnName")).replaceAll("\\([^\\.]*\\)", "");
						 valueCSelect = (String) yAxisValueObject.get("aggColumnName");
					}
				}
			}
			yAxisArray = valueColumnName.split("[.]", 0);
			String columnName=null;
			if (yAxisArray.length > 1) {
				columnName = yAxisArray[1].replace(")", "");
			}
			String tableName = String.valueOf(dataObj.get("table"));
		//	String columnName = ColumnStr.replace(")","");
			String type = valueCSelect.replaceAll("\\(.*", "");
			if("UNIQUECOUNT".equals(type))
		      type="COUNT";
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

			if(Lebel == null) {
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

			if (filterCondition != null && !"".equalsIgnoreCase(filterCondition)
					&& !"null".equalsIgnoreCase(filterCondition)) {
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
			if (whereCondQuery != null && !"".equalsIgnoreCase(whereCondQuery)
					&& !"null".equalsIgnoreCase(whereCondQuery)) {
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
			if (tableName != null && !"".equalsIgnoreCase(tableName) && !"null".equalsIgnoreCase(tableName)
					&& columnName != null && !"".equalsIgnoreCase(columnName) && !"null".equalsIgnoreCase(columnName)) {

				long fromCount = 0;
				long toCount = 0;
				String percent = "";
				if (fromWhereCondQuery != null && !"".equalsIgnoreCase(fromWhereCondQuery) && toWhereCondQuery != null
						&& !"".equalsIgnoreCase(toWhereCondQuery)) {
					if (fromWhereCondQuery != null && !"".equalsIgnoreCase(fromWhereCondQuery)) {
						String fromQuery = "SELECT " + type + "(" + columnName + ") FROM " + tableName
								+ fromWhereCondQuery;
                   System.out.println("line 238 "+ fromQuery);
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
						System.out.println("line 248 "+ toQuery);
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
					if("SALESDATA".equals(tableName))
						tableName="SALES_DATA";
					if (type != null && !"".equalsIgnoreCase(type) && !"UniqueCount".equals(type)) {
						selectQuery = "SELECT " + type + "(" + columnName + ") FROM " + tableName + whereCondQuery;
					} else {
//	                    SELECT COUNT(*) AS VALUE FROM (SELECT  DISTINCT COMMODITY FROM  V_MAND_ATTR_VIEW )
						selectQuery = "SELECT COUNT(*) AS VALUE FROM (SELECT  DISTINCT " + columnName + " FROM "
								+ tableName + whereCondQuery + ")";
//	                   selectQuery = "SELECT  " + type + "(" + columnName + ") FROM " + tableName + whereCondQuery;
					}
					if (!("SALES_HAL7".equals(tableName)) && !("SALESDATA2".equals(tableName) ) && !("SALES_HAL27".equals(tableName))) {
						System.out.println("line 277 "+ selectQuery);
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
				}}
				tabledataobj.put("result", result);
			}  //som
		}catch(Exception e)
		{
			logger.error("An error occurred in fetchHomeCardDetails method.", e.getMessage());
		}
		return tabledataobj;
	}

	public String withSuffix(long count) {
		if (count < 1000) {
			return "" + count;
		}
		int exp = (int) (Math.log(count) / Math.log(1000));
		return String.format("%.1f %c", count / Math.pow(1000, exp), "KMGTPE".charAt(exp - 1));
	}


	@Transactional(rollbackFor = Exception.class)
	public JSONObject getChartDataList( Map<String,String> dataObj, InputParams ip) {
		JSONObject chartListObj = new JSONObject();
		JSONArray chartDataArr = new JSONArray();
    try{
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

			if (xAxis != null && !"".equalsIgnoreCase(xAxis)
					&& !"null".equalsIgnoreCase(xAxis) && !xAxis.isEmpty()) {
				xAxisArr = (JSONArray) JSONValue.parse(xAxis);
			}
			if (yAxis != null && !"".equalsIgnoreCase(yAxis)
					&& !"null".equalsIgnoreCase(yAxis) && !yAxis.isEmpty()) {
				yAxisArr = (JSONArray) JSONValue.parse(yAxis);
			}
			if (filterCondition != null && !"".equalsIgnoreCase(filterCondition)
					&& !"null".equalsIgnoreCase(filterCondition)) {
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
								if("UniqueCount".equalsIgnoreCase(aggColumnName))
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

			if (whereCondQuery != null && !"".equalsIgnoreCase(whereCondQuery)
					&& !"null".equalsIgnoreCase(whereCondQuery)) {
				whereCondQuery = " WHERE " + whereCondQuery;
			}

			if (isValidString(selectQuery) && tables != null && !tables.isEmpty()) {
				String tableName = tables.equals("SALESDATA") ? "SALES_DATA" : tables;
				String countQuery = "";

				selectQuery = PilogUtilities.trimChar(selectQuery, ',');
				selectQuery = "SELECT " + selectQuery + " FROM " + tableName + whereCondQuery + groupByCond + orderBy;

				countQuery = "SELECT COUNT(*) FROM " + tableName + whereCondQuery + groupByCond;
				System.out.println("selectQuery ::: " + selectQuery);
			}
			if (!("SALES_HAL7".equals(tables)) && !("SALESDATA2".equals(tables)) && !("SALES_HAL27".equals(tables))) {
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
									chartDataX=		chartDatum.toString();
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

						chartListObj.put("chartTitle", chartTitleStr);
						chartListObj.put("chartType", chartType);
						chartListObj.put("chartLevelsAndValueObj", chartDataArr);

					}
				} catch (Exception e) {
					// Handle exceptions appropriately
					logger.error("An error occurred in getChartDataList method inside if block .", e.getMessage());
				}

		}
			System.out.println("selectQuery2 :::" + selectQuery);
		} catch (Exception e) {
		logger.error("An error occurred in getChartDataList method.", e.getMessage());
		}
		return chartListObj;
	}

	@Transactional
	public String buildCondition(JSONObject paramObj) {
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
							value = "TO_DATE('" + minValue + "','DD-MM-YYYY') AND TO_DATE('" + maxvalue
									+ "','DD-MM-YYYY')";
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
							value = "STR_TO_DATE('" + minValue + "','DD-MM-YYYY') AND STR_TO_DATE('" + maxvalue
									+ "','DD-MM-YYYY')";
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
							value = "CONVERT('" + minValue + "','DD-MM-YYYY') AND CONVERT('" + maxvalue
									+ "','DD-MM-YYYY')";
						}
					} else if (dataBaseDriver.toUpperCase().contains("DB2")) {

					}

				}

			}
			if (operatorName != null && !"".equalsIgnoreCase(operatorName) && value != null
					&& !"".equalsIgnoreCase(value)) {
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

	public String generateInStr(String value) {

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
}
