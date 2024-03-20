package com.pilog.mdm.service;

import java.util.List;
import java.util.Map;

public interface IDSKnowledgeBaseService {
    List<Map<String,Long>> getKnowledgeBaseCategory();
    List<Map<String,String>> getKnowledgeBaseData(String category);
}
