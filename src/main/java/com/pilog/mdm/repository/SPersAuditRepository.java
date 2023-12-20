package com.pilog.mdm.repository;

import com.pilog.mdm.model.SPersAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SPersAuditRepository extends JpaRepository<SPersAudit,String> {
    SPersAudit findBySessionIdAndFlag(String jwt, String n);
}
