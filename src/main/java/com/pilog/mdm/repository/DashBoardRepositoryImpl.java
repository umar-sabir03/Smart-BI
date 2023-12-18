package com.pilog.mdm.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class DashBoardRepositoryImpl implements IDashBoardRepository {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<Object> findResultList(String dynamicQuery) {
        Query query = entityManager.createNativeQuery(dynamicQuery);
        List resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<Object> findFirst10ResultList(String selectQuery) {
        Query query = entityManager.createNativeQuery(selectQuery);
         query = query.setMaxResults(10);
        List resultList = query.getResultList();
        return resultList;
    }
}
