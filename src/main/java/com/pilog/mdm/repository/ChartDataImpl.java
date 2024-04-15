package com.pilog.mdm.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ChartDataImpl implements ChartDataRepository{

    private final JdbcTemplate jdbcTemplate;
    @Override
    public List<Map<String, Object>> getChartData(String nativeQuery) {
        try {
            return jdbcTemplate.queryForList(nativeQuery);
        } catch (Exception e) {
            return Collections.emptyList(); // or return some default value
        }
    }

    @Override
    public List<String> getHomePageFilterData(String chartType) {
        try {
        String nativeQuery="SELECT DISTINCT TABLE_NAME FROM O_RECORD_VISUALIZATION  " +
                             "WHERE " +
//                           "ROLE_ID =:ROLE_ID AND ORGN_ID=:ORGN_ID AND " +
                "CHART_TYPE NOT IN('CARD','FILTER','COMPARE_FILTER') AND  DASHBORD_NAME =? " ;

          return jdbcTemplate.queryForList(nativeQuery, String.class, chartType);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getHomePageFilterTableColumns(String tableName) {
        try {
            String nativeQuery= "SELECT COLUMN_NAME FROM USER_TAB_COLUMNS WHERE TABLE_NAME =?";
            return jdbcTemplate.queryForList(nativeQuery, String.class, tableName);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public String getHomePageFilterColumn(String chartType) {
        try {
            String nativeQuery = "SELECT FILTER_COLUMN FROM O_RECORD_VISUALIZATION " +
                    "WHERE  CHART_TYPE =? AND DASHBORD_NAME =?";
            return jdbcTemplate.queryForObject(nativeQuery, String.class, "FILTER",chartType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
