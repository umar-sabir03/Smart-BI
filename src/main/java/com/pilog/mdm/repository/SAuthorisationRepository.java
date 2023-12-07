package com.pilog.mdm.repository;

import com.pilog.mdm.model.SAuthorisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SAuthorisationRepository extends JpaRepository<SAuthorisation, String>{
}
