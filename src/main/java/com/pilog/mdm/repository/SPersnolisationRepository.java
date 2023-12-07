package com.pilog.mdm.repository;

import com.pilog.mdm.model.SPersnolisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SPersnolisationRepository extends JpaRepository<SPersnolisation, String> {

}
