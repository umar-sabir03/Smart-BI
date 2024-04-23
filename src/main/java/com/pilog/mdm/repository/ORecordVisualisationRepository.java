package com.pilog.mdm.repository;

import com.pilog.mdm.model.ORecordVisualisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ORecordVisualisationRepository extends JpaRepository<ORecordVisualisation,String> {
    @Query("SELECT DISTINCT dashboardName " +
            "FROM ORecordVisualisation " +
            "WHERE roleId = :roleId " +
            "ORDER BY dashboardName")
    List<String> findDashBoardNameByRoleId(String roleId);


    List<ORecordVisualisation> findByDashboardNameAndRoleIdOrderByChartSequenceNo(String dashbordname, String mmManager);

    @Transactional
    @Modifying
    @Query("UPDATE ORecordVisualisation SET filterColumn = :columns WHERE chartType = :chartType AND dashboardName = :dashboardName")
    void updateFilterColumn(@Param("columns") String columns, @Param("chartType") String chartType, @Param("dashboardName") String dashboardName);

}
