package com.pilog.mdm.service;


import com.pilog.mdm.repository.ORecordVisualisationRepository;
import com.pilog.mdm.requestdto.InputParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DashBoardsService {

	@Autowired
	public ORecordVisualisationRepository orecordRepo;

	public List<String> getChartData( ) {
		List<String> dashboardName = orecordRepo.findDashBoardNameByRoleId("MM_MANAGER");
		return dashboardName;
	}



}
