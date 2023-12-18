package com.pilog.mdm.controller;

import com.pilog.mdm.requestdto.InputParams;
import com.pilog.mdm.service.DashBoardsService;
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

@RestController
@RequestMapping(value = "/charts")
public class DashBoardsController {

	@Autowired
	public DashBoardsService dashBoardsService;


	private static final Logger logger = LoggerFactory.getLogger(DashBoardsController.class);

	@RequestMapping(value = "/")
	public String testHello() {
		return "home";
	}

	@GetMapping(value = "/getChartNames")
	public ResponseEntity<Map<String,List<String>>> getChartCategoryNames() {
		Map<String,List<String>> result=new HashMap<>();
		try {
			List<String> chartCategoryNames = dashBoardsService.getChartCategoryNames();
			result.put("chartCategoryNames", chartCategoryNames);
			logger.info("Successfully retrieved chart data: {}", chartCategoryNames);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while fetching chart data: {}", e.getMessage(), e);
			throw new RuntimeException("Something Went Wrong...");
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PostMapping("/getChartData")
	public ResponseEntity<Map<String,Object>> fetchChartData(HttpServletRequest request, @RequestBody InputParams ip) {
		Map<String,Object> chartCards=new HashMap<>();
		try {
		 chartCards = dashBoardsService.getChartCards( ip);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(chartCards,HttpStatus.OK);
	}

}
