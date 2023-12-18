package com.pilog.mdm.repository;

import com.pilog.mdm.model.SPersDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SPersDetailRepository extends JpaRepository<SPersDetail, String>{

//	Optional<SPersDetail> findByUserNameIgnoreCase(String userName);
	SPersDetail findByUserNameIgnoreCase(String userName);
	
}
