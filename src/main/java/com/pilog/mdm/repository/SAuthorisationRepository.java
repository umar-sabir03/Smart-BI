package com.pilog.mdm.repository;

import com.pilog.mdm.model.SAuthorisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface SAuthorisationRepository extends JpaRepository<SAuthorisation, String>{
    @Modifying
    @Transactional
    @Query(value = "UPDATE s_authorisation SET pass_phrase = :passPhrase WHERE pers_id = :persId", nativeQuery = true)
    void updatePassPhrase(@Param("persId") String persId, @Param("passPhrase") String passPhrase);
}
