package com.pilog.mdm.controller;

import com.pilog.mdm.service.IDSKnowledgeBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${api-version}")
public class DSKnowledgeBaseController {

    private final IDSKnowledgeBaseService dsKnowledgeBaseService;

    @GetMapping("/knowledgeBaseCategory")
    public ResponseEntity<List<Map<String,Long>>> getKnowledgeBaseCategory(){
        List<Map<String,Long>> result=new ArrayList<>();
        result  = dsKnowledgeBaseService.getKnowledgeBaseCategory();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @PostMapping("/knowledgeBaseData")
    public ResponseEntity<List<Map<String,String>>> getKnowledgeBaseData(@RequestBody String category){
       List<Map<String,String>> result=new ArrayList<>();
        List<Map<String,String>> response=new ArrayList<>();
        result  = dsKnowledgeBaseService.getKnowledgeBaseData(category);
        result.forEach(map -> {
            Map<String, String> modifiedMap = new HashMap<>();
            map.forEach((key, value) -> {
                modifiedMap.put(key.replace("_", " "), value);
            });
            response.add(modifiedMap);
        });
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
