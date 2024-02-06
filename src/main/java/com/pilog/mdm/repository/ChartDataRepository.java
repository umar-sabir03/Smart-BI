package com.pilog.mdm.repository;

import java.util.List;
import java.util.Map;

public interface ChartDataRepository {
    List<Map<String, Object>> getChartData(String nativeQuery) ;
}
