package com.pilog.mdm.controller;

import com.pilog.mdm.requestdto.InputParams;
import com.pilog.mdm.service.IDashBoardsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(value = "/charts")
@RequiredArgsConstructor
public class DashBoardsController {

	@Autowired
	private IDashBoardsService dashBoardsService;



	private static final Logger logger = LoggerFactory.getLogger(DashBoardsController.class);

	@RequestMapping(value = "/")
	public String testHello() {
		return "home";
	}

	@GetMapping(value = "/getChartNames")
	public ResponseEntity<Map<String,List<String>>> getChartCategoryNames() {
		Map<String,List<String>> result=new HashMap<>();
			List<String> chartCategoryNames = dashBoardsService.getChartCategoryNames();
			result.put("chartCategoryNames", chartCategoryNames);
			logger.info("Successfully retrieved chart data: {}", chartCategoryNames);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PostMapping("/getChartData")
	public ResponseEntity<Map<String,Object>> fetchChartData(HttpServletRequest request, @RequestBody InputParams ip) {
//		 dashBoardsService.getChartCards( ip);
		Map<String,Object> chartCards=dashBoardsService.getChartDataAndCardData( ip);
		return new ResponseEntity<>(chartCards,HttpStatus.OK);
	}

	@PostMapping("/getDropdownData")
	public ResponseEntity<Set<String>> fetchChartData(@RequestParam String columnName,@RequestParam String tableName) {
		Set<String> dropdownData = dashBoardsService.getColumnData(columnName,tableName);

		return new ResponseEntity<>(dropdownData,HttpStatus.OK);
	}

}
