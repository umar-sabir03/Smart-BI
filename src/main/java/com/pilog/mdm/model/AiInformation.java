package com.pilog.mdm.model;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

    @Entity
    @Table(name = "AI_INFORMATION")
    @Data
    public class AiInformation {

        @Id
        @Column(name = "AUDIT_ID", length = 100)
        private String auditId;

        @Column(name = "CATEGORY", length = 500)
        private String category;

        @Column(name = "DEFINITION", length = 4000)
        private String definition;

        @Column(name = "FUNCTION_DEFINATION", length = 4000)
        private String functionDefinition;

        @Column(name = "FUNCTIONS_CONCEPTS", length = 1000)
        private String functionsConcepts;

        @Column(name = "USE_CASES", length = 1000)
        private String useCases;

    }


