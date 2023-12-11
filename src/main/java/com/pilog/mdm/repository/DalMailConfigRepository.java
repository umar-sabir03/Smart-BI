package com.pilog.mdm.repository;

import com.pilog.mdm.model.DalMailConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface DalMailConfigRepository extends JpaRepository<DalMailConfig, String>{

	Optional<DalMailConfig> findByOrgnId(String orgnId);
}
