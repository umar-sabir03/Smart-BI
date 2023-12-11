package com.pilog.mdm.controller;

import com.pilog.mdm.service.DashBoardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/charts")
public class DashBoardsController {

	@Autowired
	public DashBoardsService dashBoardsService;

	@RequestMapping(value = "/")
	public String testHello() {
		return "home";
	}

	@GetMapping(value = "/getChartData")
	public ResponseEntity<Map<String,List<String>>> getChartData() {
		Map<String,List<String>> result=new HashMap<>();
		try {
			List<String> chartData = dashBoardsService.getChartData();
			result.put("dashBordlist", chartData);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Something Went Wrong...");
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}
