package com.pilog.mdm.repository;

import java.util.Set;

public interface IDynamicColumnDataRepository {
    Set<String>  getColumnData(String columnName,String tableName);
}
