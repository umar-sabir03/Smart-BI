package com.pilog.mdm.service;

import com.pilog.mdm.requestdto.InputParams;

import java.util.List;
import java.util.Map;
import java.util.Set;



public interface IDashBoardsService {

	 Map<String,Object> getChartCards(InputParams ip) ;
	 Set<String> getColumnData(String columnName,String tableName) ;
	List<String> getChartCategoryNames();

	Map<String,Object> getChartDataAndCardData(InputParams ip) ;
}
