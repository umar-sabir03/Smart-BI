package com.pilog.mdm.repository;

import com.pilog.mdm.model.SPersDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SPersDetailRepository extends JpaRepository<SPersDetail, String>{

	SPersDetail findByUserNameIgnoreCase(String userName);

	Optional<SPersDetail> findByUserName(String email);
	
}
