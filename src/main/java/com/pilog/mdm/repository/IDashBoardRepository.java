package com.pilog.mdm.repository;

import java.util.List;


public interface IDashBoardRepository {
    List<Object> findResultList(String query);

    List<Object>  findFirst10ResultList(String selectQuery);
}
