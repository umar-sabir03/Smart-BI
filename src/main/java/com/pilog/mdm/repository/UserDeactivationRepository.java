package com.pilog.mdm.repository;

import com.pilog.mdm.model.UserDeactivation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDeactivationRepository extends JpaRepository<UserDeactivation,Long> {
    UserDeactivation findByUserName(String userName);
}
