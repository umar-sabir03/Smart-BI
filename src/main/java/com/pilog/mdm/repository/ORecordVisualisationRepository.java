package com.pilog.mdm.repository;

import com.pilog.mdm.model.ORecordVisualisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ORecordVisualisationRepository extends JpaRepository<ORecordVisualisation,String> {
    @Query("SELECT DISTINCT dashboardName " +
            "FROM ORecordVisualisation " +
            "WHERE roleId = :roleId " +
            "ORDER BY dashboardName")
    List<String> findDashBoardNameByRoleId(String roleId);


    List<ORecordVisualisation> findByDashboardNameAndRoleIdOrderByChartSequenceNo(String dashbordname, String mmManager);
}
