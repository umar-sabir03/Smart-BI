package com.pilog.mdm.requestdto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class InputParams {
	private String dashbordname;
	private String conditionTable;
	private Map<String, List<String>> conditions;

}
