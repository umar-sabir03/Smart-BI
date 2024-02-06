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
}
