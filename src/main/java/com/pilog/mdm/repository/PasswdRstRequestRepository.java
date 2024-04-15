package com.pilog.mdm.repository;



import com.pilog.mdm.model.PasswdRstRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswdRstRequestRepository extends JpaRepository<PasswdRstRequest, Long> {
    PasswdRstRequest findByOtp(String token);

    Optional<PasswdRstRequest> findByUserNameAndOtp(String userName,String Otp);
}
