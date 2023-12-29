package com.pilog.mdm.repository;

import com.pilog.mdm.model.SPersAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SPersAuditRepository extends JpaRepository<SPersAudit,String> {
    SPersAudit findBySessionIdAndFlag(String jwt, String n);
}
