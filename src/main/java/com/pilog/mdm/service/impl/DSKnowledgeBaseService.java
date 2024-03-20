package com.pilog.mdm.service.impl;

import com.pilog.mdm.repository.AiInformationRepo;
import com.pilog.mdm.service.IDSKnowledgeBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class DSKnowledgeBaseService implements IDSKnowledgeBaseService {

    private final AiInformationRepo aiInformationRepo;
    @Override
    public List<Map<String, Long>> getKnowledgeBaseCategory() {
        List<Map<String, Long>> categoryWithCount = aiInformationRepo.getCategoryWithCount();
        return categoryWithCount;
    }

    @Override
    public List<Map<String,String>> getKnowledgeBaseData(String category) {
        return aiInformationRepo.findByCategory(category);
    }
}
