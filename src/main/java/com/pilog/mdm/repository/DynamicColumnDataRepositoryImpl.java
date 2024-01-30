package com.pilog.mdm.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class DynamicColumnDataRepositoryImpl implements IDynamicColumnDataRepository {

    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public Set<String> getColumnData(String columnName, String tableName) {
         Set<String> resp=new HashSet<>();
        String jpql = "SELECT c." + columnName.replaceAll(" ","_") + " FROM " + tableName + " c";

        List resultList = entityManager.createNativeQuery(jpql)
                .getResultList();
        for(Object result:resultList){
            resp.add( (String) result);
        }
        return resp;
    }
}
