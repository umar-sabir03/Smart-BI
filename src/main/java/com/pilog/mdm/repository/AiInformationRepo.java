package com.pilog.mdm.repository;

import com.pilog.mdm.model.AiInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface AiInformationRepo extends JpaRepository<AiInformation,String> {
    @Query("SELECT e.category as category, COUNT(e) as count FROM AiInformation e  GROUP BY e.category")
    List<Map<String,Long>> getCategoryWithCount();

    @Query("SELECT e.definition as  Category_Definition, " +
            "functionsConcepts as Function_Name, " +
            "functionDefinition as Definition, " +
            "useCases as UserCases FROM AiInformation e WHERE e.category = :category")
    List<Map<String,String>> findByCategory(String category);
}
