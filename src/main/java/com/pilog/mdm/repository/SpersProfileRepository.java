package com.pilog.mdm.repository;

import com.pilog.mdm.model.SPersProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpersProfileRepository extends JpaRepository<SPersProfile,String> {
    @Query("Select Distinct roleId from SPersProfile where persId=:persId")
  List<String> findByPersId(String persId);
    @Query("Select Distinct roleId from SPersProfile ")
    List<String> findAllRoles( );
}
