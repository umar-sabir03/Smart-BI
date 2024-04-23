package com.pilog.mdm.repository;

import com.pilog.mdm.model.UserDeactivation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDeactivationRepository extends JpaRepository<UserDeactivation,Long> {
    Optional<UserDeactivation> findByUserName(String userName);
}
