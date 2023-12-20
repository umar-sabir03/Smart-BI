package com.pilog.mdm.repository;

import com.pilog.mdm.model.SPersonnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SPersonnelRepository extends JpaRepository<SPersonnel, String> {
    SPersonnel  findTop1ByPersIdOrderByCreateDate(String PersId);
}
